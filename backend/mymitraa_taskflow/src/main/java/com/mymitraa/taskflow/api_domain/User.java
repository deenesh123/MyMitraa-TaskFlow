package com.mymitraa.taskflow.api_domain;

import org.springframework.data.mongodb.core.mapping.Document;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
public class User extends BaseDomain {

    private String username;
    private String password;
    private String email;
    private String name;
    private String role;
    private String team;
    private String status = "Active";
    private String avatar;
    private String createdDate;
    private String lastLogin;
    private String department;
    private String designation;
    private Set<String> permissions = new HashSet<>();
    private boolean enabled = true;
    private Set<String> roles = new HashSet<>();

    public String getRole() {
        return role != null ? role : (roles != null && !roles.isEmpty() ? roles.iterator().next() : "Engineer");
    }

    public void setRole(String role) {
        this.role = role;
        if (role != null) {
            this.roles.add(role);
        }
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getStatus() {
        return status != null ? status : "Active";
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCreated_date() {
        return createdDate;
    }

    public void setCreated_date(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLast_login() {
        return lastLogin;
    }

    public void setLast_login(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Set<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }
}