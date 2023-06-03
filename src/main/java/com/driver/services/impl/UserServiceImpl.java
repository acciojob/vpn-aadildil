package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;



    @Override
    public User register(String username, String password, String countryName) throws Exception{

         countryName=countryName.toUpperCase();
        if(!isCountryPresent(countryName))
            throw new Exception("Country not found");
        CountryName countryName1=CountryName.valueOf(countryName);//enum countryName
        Country country=new Country(countryName1,countryName1.toCode());//creating country model

        User user=new User(username,password,country);
        country.setUser(user);
        countryRepository3.save(country);
        int userId=country.getUser().getId();

        String countryCode=country.getCode();
        String userIp=countryCode+"."+userId;
        country.getUser().setOriginalIp(userIp);
        countryRepository3.save(country);
        return user;



    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) throws Exception {

        Optional<User> optionalUser=userRepository3.findById(userId);
        if(!optionalUser.isPresent())
            throw new Exception("User not found");
        Optional<ServiceProvider> optionalServiceProvider=serviceProviderRepository3.findById(serviceProviderId);
        if(!optionalServiceProvider.isPresent())
            throw new Exception("Service provider not found");

        User user=optionalUser.get();//user
        ServiceProvider serviceProvider=optionalServiceProvider.get();//service provider

        serviceProvider.getUsers().add(user);
        user.getServiceProviderList().add(serviceProvider);
        serviceProviderRepository3.save(serviceProvider);


        return user;
    }

    private boolean isCountryPresent(String countryname) {


        try {
            CountryName country = CountryName.valueOf(countryname);
            return true;
        } catch (IllegalArgumentException e) {
            // Exception will be thrown if the input string is not present in the enum
            return false;
        }
    }
}
