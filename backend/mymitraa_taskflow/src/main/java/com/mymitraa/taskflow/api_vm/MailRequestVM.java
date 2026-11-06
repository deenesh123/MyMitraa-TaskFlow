package com.mymitraa.taskflow.api_vm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailRequestVM {
    private String to;
    private List<String> recipients;
    private List<String> cc;
    private String subject;
    private String content;
    private String email;
    private String projectName;
    private String weekNumber;
    private String milestonesCompleted;
    private String highlights;
    private String nextWeekGoals;
}
