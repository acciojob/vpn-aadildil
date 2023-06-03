package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {

        Admin admin=new Admin(username,password);
        adminRepository1.save(admin);
        return  admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {

        Optional<Admin> adminOptional=adminRepository1.findById(adminId);
//        if(!adminOptional.isPresent())
//        throw new Exception("admn not found");
        Admin admin=adminOptional.get();

        ServiceProvider serviceProvider=new ServiceProvider(providerName);
        serviceProvider.setAdmin(admin);
        admin.getServiceProviders().add(serviceProvider);
        adminRepository1.save(admin);
        return admin;


    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryname) throws Exception{

        Optional<ServiceProvider> optionalServiceProvider=serviceProviderRepository1.findById(serviceProviderId);
        ServiceProvider serviceProvider=optionalServiceProvider.get();


        String countryName1=countryname.toUpperCase();
        if(!isCountryPresent(countryName1))
            throw new Exception("Country not found");
        CountryName countryName=CountryName.valueOf(countryName1);//enum countryName
        Country country=new Country(countryName,countryName.toCode());//creating country model
        country.setServiceProvider(serviceProvider);
        serviceProvider.getCountryList().add(country);
        serviceProviderRepository1.save(serviceProvider);
        return serviceProvider;




    }

    public boolean isCountryPresent(String countryname) {

        boolean isPresent=false;
        try {
            CountryName country = CountryName.valueOf(countryname);
            isPresent = true;
            return true;
        } catch (IllegalArgumentException e) {
            // Exception will be thrown if the input string is not present in the enum
            return false;
        }
    }

}
