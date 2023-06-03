package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.AdminService;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Autowired
    AdminServiceImpl adminService;

    @Override
    public User connect(int userId, String countryName) throws Exception{

        //getting user by ID;
        Optional<User> optionalUser =userRepository2.findById(userId);
        //checking country name exist or not
        countryName=countryName.toUpperCase();
        if(!adminService.isCountryPresent(countryName))
            throw new Exception("Country not found");
        CountryName countryName1=CountryName.valueOf(countryName);
        User user=optionalUser.get();
        if(user.isConnected()||countryName1.equals(user.getCountry().getCountryName()))
            return user;


        List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
        if(serviceProviderList.size()==0)
            throw new Exception("Unable to connect");

       Optional<ServiceProvider> validServiceProviderOptional=getValidServiceProvider(countryName1,serviceProviderList);
       if(!validServiceProviderOptional.isPresent())
           throw new Exception("Unable to connect");
       ServiceProvider validServiceProvider=validServiceProviderOptional.get();

       // "updatedCountryCode.serviceProviderId.userId"
        String maskedIP=countryName1.toCode()+"."+validServiceProvider.getId()+"."+userId;


        Connection connection=new Connection(user,validServiceProvider);
        validServiceProvider.getConnectionList().add(connection);
        serviceProviderRepository2.save(validServiceProvider);
        user.getConnectionList().add(validServiceProvider.getConnectionList().get(validServiceProvider.getConnectionList().size()-1));
        user.setConnected(true);
        user.setMaskedIP(maskedIP);
        userRepository2.save(user);




        //serviceProviderRepository2.save(validServiceProvider);

        return user;





    }



    @Override
    public User disconnect(int userId) throws Exception {

        //getting user by ID;
        Optional<User> optionalUser =userRepository2.findById(userId);
        User user=optionalUser.get();
        if(!user.isConnected())
            throw new Exception("Already disconnected");
        user.setConnected(false);
        user.setMaskedIP(null);
        userRepository2.save(user);




        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {

        Optional<User> optionalSender=userRepository2.findById(senderId);
        Optional<User> optionalReceiver=userRepository2.findById(receiverId);
        if(!optionalReceiver.isPresent()||!optionalSender.isPresent())
            throw new Exception("Cannot establish communication");

        User sender=optionalSender.get();
        User receiver=optionalReceiver.get();

        Country senderCountry=sender.getCountry();
        String senderCountryName=senderCountry.getCountryName().name();
        String receiverCountryName="";
        if(!receiver.isConnected())
            receiverCountryName=receiver.getCountry().getCountryName().name();
        else
        {
            String countryCode= receiver.getMaskedIP().substring(0,3);
            for(CountryName countryName:CountryName.values())
            {
                String code=countryName.toCode();
                if(code.equals(countryCode))
                {
                    receiverCountryName=countryName.name();
                }
            }
        }

        if(senderCountryName.equals(receiverCountryName))
            return sender;

        User user=connect(senderId,receiverCountryName);
        if(!user.isConnected())
            throw new Exception("Cannot establish communication");
        else
            return user;








    }



    private Optional<ServiceProvider> getValidServiceProvider(CountryName countryName1,
                                                              List<ServiceProvider> serviceProviderList) {

        int serviceProviderID=Integer.MAX_VALUE;
        ServiceProvider validServiceProvider=null;


        for(ServiceProvider serviceProvider:serviceProviderList)
        {
            for(Country country: serviceProvider.getCountryList())
            {
                if(country.getCountryName()==countryName1)
                {
                    if(serviceProviderID>serviceProvider.getId())
                    {
                        validServiceProvider=serviceProvider;
                        serviceProviderID=serviceProvider.getId();

                    }
                }
            }
        }
        if(validServiceProvider==null)
            return Optional.empty();
        return Optional.of(validServiceProvider);
    }
}
