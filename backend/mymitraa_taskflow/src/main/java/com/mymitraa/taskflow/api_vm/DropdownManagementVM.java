package com.mymitraa.taskflow.api_vm;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DropdownManagementVM {

    private String id;

    @NotBlank(message = "Domain name is required")
    @Size(max = 100, message = "Domain name must not exceed 100 characters")
    private String domainName;

    @NotBlank(message = "Value is required")
    @Size(max = 200, message = "Value must not exceed 200 characters")
    private String value;

    @Size(max = 100, message = "Sub value must not exceed 100 characters")
    private String subValue;

    @Size(max = 100, message = "Module must not exceed 100 characters")
    private String module;

    @Size(max = 100, message = "Sub module must not exceed 100 characters")
    private String subModule;

    @Size(max = 500, message = "Comments must not exceed 500 characters")
    private String comments;
}