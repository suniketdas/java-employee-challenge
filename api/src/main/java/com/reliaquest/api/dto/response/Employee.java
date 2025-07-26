package com.reliaquest.api.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Employee {
    private String id;
    private String name;
    private Integer salary;
    private Integer age;
    private String title;
    private String email;
}
