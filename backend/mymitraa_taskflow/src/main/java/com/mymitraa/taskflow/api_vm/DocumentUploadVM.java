package com.mymitraa.taskflow.api_vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadVM {
    private String id;
    private String documentType;
    private String description;
    private MultipartFile file;
    private String fileName;
}
