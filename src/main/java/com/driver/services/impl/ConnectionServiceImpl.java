package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
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


//        //getting user by ID;
//        Optional<User> optionalUser =userRepository2.findById(userId);
//        //checking country name exist or not
//        countryName=countryName.toUpperCase();
//        Country country=adminService.createCountry(countryName);
//        CountryName countryName1=CountryName.valueOf(countryName);
//        User user=optionalUser.get();
//        if(user==null)
//            return null;
//
//        if(user.getMaskedIp()!=null||countryName1.equals(user.getOriginalCountry().getCountryName()))
//            return user;
//
//
//        List<ServiceProvider> serviceProviderList=user.getServiceProviderList();
//        if(serviceProviderList.size()==0)
//            throw new Exception("Unable to connect");
//
//       Optional<ServiceProvider> validServiceProviderOptional=getValidServiceProvider(countryName1,serviceProviderList);
//       if(!validServiceProviderOptional.isPresent())
//           throw new Exception("Unable to connect");
//       ServiceProvider validServiceProvider=validServiceProviderOptional.get();
//
//       // "updatedCountryCode.serviceProviderId.userId"
//        String maskedIP=countryName1.toCode()+"."+validServiceProvider.getId()+"."+userId;
//
//
//        Connection connection=new Connection(user,validServiceProvider);
//        validServiceProvider.getConnectionList().add(connection);
//        serviceProviderRepository2.save(validServiceProvider);
//        user.getConnectionList().add(validServiceProvider.getConnectionList().get(validServiceProvider.getConnectionList().size()-1));
//        user.setConnected(true);
//        user.setMaskedIp(maskedIP);
//        userRepository2.save(user);
//
//
//
//
//        //serviceProviderRepository2.save(validServiceProvider);
//
//        return user;

        User user = userRepository2.findById(userId).get();
        if(user.getMaskedIp()!=null){
            throw new Exception("Already connected");
        }
        else if(countryName.equalsIgnoreCase(user.getOriginalCountry().getCountryName().toString())){
            return user;
        }
        else {
            if (user.getServiceProviderList()==null){
                throw new Exception("Unable to connect");
            }

            List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
            int a = Integer.MAX_VALUE;
            ServiceProvider serviceProvider = null;
            Country country =null;

            for(ServiceProvider serviceProvider1:serviceProviderList){

                List<Country> countryList = serviceProvider1.getCountryList();

                for (Country country1: countryList){

                    if(countryName.equalsIgnoreCase(country1.getCountryName().toString()) && a > serviceProvider1.getId() ){
                        a=serviceProvider1.getId();
                        serviceProvider=serviceProvider1;
                        country=country1;
                    }
                }
            }
            if (serviceProvider!=null){
                Connection connection = new Connection();
                connection.setUser(user);
                connection.setServiceProvider(serviceProvider);

                String cc = country.getCode();
                int givenId = serviceProvider.getId();
                String mask = cc+"."+givenId+"."+userId;

                user.setMaskedIp(mask);
                user.setConnected(true);
                user.getConnectionList().add(connection);

                serviceProvider.getConnectionList().add(connection);

                userRepository2.save(user);
                serviceProviderRepository2.save(serviceProvider);


            }
        }
        return user;





    }



    @Override
    public User disconnect(int userId) throws Exception {

        //getting user by ID;
        Optional<User> optionalUser =userRepository2.findById(userId);
        User user=optionalUser.get();
        if(user.getConnected()==false)
            throw new Exception("Already disconnected");
        user.setConnected(false);
        user.setMaskedIp(null);
        userRepository2.save(user);




        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {

//        Optional<User> optionalSender=userRepository2.findById(senderId);
//        Optional<User> optionalReceiver=userRepository2.findById(receiverId);
//        if(!optionalReceiver.isPresent()||!optionalSender.isPresent())
//            throw new Exception("Cannot establish communication");
//
//        User sender=optionalSender.get();
//        User receiver=optionalReceiver.get();
//
//        Country senderCountry=sender.getOriginalCountry();
//        String senderCountryName=senderCountry.getCountryName().name();
//        String receiverCountryName="";
//        if(receiver.getConnected()==false)
//            receiverCountryName=receiver.getOriginalCountry().getCountryName().name();
//        else
//        {
//            String countryCode= receiver.getMaskedIp().substring(0,3);
//            for(CountryName countryName:CountryName.values())
//            {
//                String code=countryName.toCode();
//                if(code.equals(countryCode))
//                {
//                    receiverCountryName=countryName.name();
//                }
//            }
//        }
//
//        if(senderCountryName.equals(receiverCountryName))
//            return sender;
//
//        User user=connect(senderId,receiverCountryName);
//        if(user.getConnected()==false)
//            throw new Exception("Cannot establish communication");
//        else
//            return user;
//
//
//
//
//
        User user = userRepository2.findById(senderId).get();
        User user1 = userRepository2.findById(receiverId).get();

        if (user1.getMaskedIp() != null) {
            String str = user1.getMaskedIp();
            String cc = str.substring(0, 3);

            if (cc.equals(user.getOriginalCountry().getCode()))
                return user;
            else {
                String countryName = "";

                if (cc.equalsIgnoreCase(CountryName.IND.toCode()))
                    countryName = CountryName.IND.toString();
                if (cc.equalsIgnoreCase(CountryName.USA.toCode()))
                    countryName = CountryName.USA.toString();
                if (cc.equalsIgnoreCase(CountryName.JPN.toCode()))
                    countryName = CountryName.JPN.toString();
                if (cc.equalsIgnoreCase(CountryName.CHI.toCode()))
                    countryName = CountryName.CHI.toString();
                if (cc.equalsIgnoreCase(CountryName.AUS.toCode()))
                    countryName = CountryName.AUS.toString();

                User user2 = connect(senderId, countryName);
                if (!user2.getConnected()) {
                    throw new Exception("Cannot establish communication");

                } else return user2;
            }

        } else {
            if (user1.getOriginalCountry().equals(user.getOriginalCountry())) {
                return user;
            }
            String countryName = user1.getOriginalCountry().getCountryName().toString();
            User user2 = connect(senderId, countryName);
            if (!user2.getConnected()) {
                throw new Exception("Cannot establish communication");
            } else return user2;

        }



    }



    private Optional<ServiceProvider> getValidServiceProvider(CountryName countryName1,
                                                              List<ServiceProvider> serviceProviderList) {

        int serviceProviderID=Integer.MAX_VALUE;
        ServiceProvider validServiceProvider=null;


        for(ServiceProvider serviceProvider:serviceProviderList)
        {
            for(Country country: serviceProvider.getCountryList())
            {
                if(country.getCountryName().name().equals(countryName1.name()))
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
