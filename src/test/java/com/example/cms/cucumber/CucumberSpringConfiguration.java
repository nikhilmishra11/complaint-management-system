package com.example.cms.cucumber;

import com.example.cms.CmsApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

@CucumberContextConfiguration
@SpringBootTest(classes = CmsApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CucumberSpringConfiguration {
}
