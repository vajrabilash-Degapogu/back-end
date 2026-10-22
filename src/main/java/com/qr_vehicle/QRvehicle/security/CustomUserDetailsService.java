package com.qr_vehicle.QRvehicle.security;

import com.qr_vehicle.QRvehicle.entity.Admin;
import com.qr_vehicle.QRvehicle.entity.CustomerAccount;
import com.qr_vehicle.QRvehicle.repository.AdminRepository;
import com.qr_vehicle.QRvehicle.repository.CustomerAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final AdminRepository adminRepository;
    private final CustomerAccountRepository customerAccounts;

    public CustomUserDetailsService(AdminRepository adminRepository, CustomerAccountRepository customerAccounts) {
        this.adminRepository = adminRepository;
        this.customerAccounts = customerAccounts;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (admin != null) {
            return new User(admin.getUsername(), admin.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        }
        CustomerAccount customer = customerAccounts.findByUsernameIgnoreCase(username)
            .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
        return new User(customer.getUsername(), customer.getPassword(),
            List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
    }
}
