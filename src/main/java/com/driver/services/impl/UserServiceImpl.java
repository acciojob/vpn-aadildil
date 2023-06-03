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

    @Autowired
    AdminServiceImpl adminService;



    @Override
    public User register(String username, String password, String countryName) throws Exception{


        if(!adminService.isCountryPresent(countryName))
            throw new Exception("Country not found");

        Country country=adminService.createCountry(countryName);//country
        User user=new User(username,password,country);//set user
        user.setConnected(false);
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
        Optional<ServiceProvider> optionalServiceProvider=serviceProviderRepository3.findById(serviceProviderId);


        User user=optionalUser.get();//user
        ServiceProvider serviceProvider=optionalServiceProvider.get();//service provider

        serviceProvider.getUsers().add(user);
        user.getServiceProviderList().add(serviceProvider);
        serviceProviderRepository3.save(serviceProvider);


        return user;
    }


}
