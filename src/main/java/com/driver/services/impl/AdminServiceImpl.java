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

        Admin admin=new Admin();
        admin.setUsername(username);
        admin.setPassword(password);


        adminRepository1.save(admin);
        return  admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {

        Optional<Admin> adminOptional=adminRepository1.findById(adminId);

        Admin admin=adminOptional.get();

        ServiceProvider serviceProvider=new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);
        admin.getServiceProviders().add(serviceProvider);
        adminRepository1.save(admin);
        return admin;


    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{

        Optional<ServiceProvider> optionalServiceProvider=serviceProviderRepository1.findById(serviceProviderId);
        ServiceProvider serviceProvider=optionalServiceProvider.get();




        if(isCountryPresent(countryName)==false)
            throw new Exception("Country not found");
        Country country=createCountry(countryName);
        country.setServiceProvider(serviceProvider);
        serviceProvider.getCountryList().add(country);
        serviceProviderRepository1.save(serviceProvider);

        return serviceProvider;




    }

    public Boolean isCountryPresent(String countryName) {////////////////

        if(countryName.equalsIgnoreCase("IND") || countryName.equalsIgnoreCase("USA") || countryName.equalsIgnoreCase("JPN") || countryName.equalsIgnoreCase("CHI") || countryName.equalsIgnoreCase("AUS"))
           return true;
      return false;
    }

    public Country createCountry(String countryName)
    {
        Country country=new Country();

        if(countryName.equalsIgnoreCase("IND")){
            country.setCountryName(CountryName.IND);
            country.setCode(CountryName.IND.toCode());
        }
        if(countryName.equalsIgnoreCase("USA")){
            country.setCountryName(CountryName.USA);
            country.setCode(CountryName.USA.toCode());
        }
        if(countryName.equalsIgnoreCase("JPN")){
            country.setCountryName(CountryName.JPN);
            country.setCode(CountryName.JPN.toCode());
        }
        if(countryName.equalsIgnoreCase("CHI")){
            country.setCountryName(CountryName.CHI);
            country.setCode(CountryName.CHI.toCode());
        }
        if(countryName.equalsIgnoreCase("AUS")){
            country.setCountryName(CountryName.AUS);
            country.setCode(CountryName.AUS.toCode());
        }
        return country;
    }

}
