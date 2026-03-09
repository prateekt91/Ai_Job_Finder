package com.jobfinder.dto;

import com.jobfinder.model.Company;

public class CompanyDto {

    private Long id;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String sector;

    public CompanyDto() {}

    public static CompanyDto fromEntity(Company company) {
        CompanyDto dto = new CompanyDto();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setAddress(company.getAddress());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setSector(company.getSector());
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }
}
