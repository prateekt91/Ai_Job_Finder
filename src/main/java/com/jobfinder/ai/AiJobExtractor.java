package com.jobfinder.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AiJobExtractor {

    private static final String SYSTEM_PROMPT = """
        You are a helpful assistant that extracts structured job information from unstructured text. 
        Given a job description, identify the following fields: 
        1. Job Title
        2. Company Name
        3. Location
        4. Salary Range (if mentioned)
        5. Job Expectations/Responsibilities
        6. Required Skills
        7. Job Type (e.g., Full-time, Part-time, Contract)
        8. Experience Level (e.g., Entry, Mid, Senior)
        9. Application Link (if mentioned)

        Return the extracted information in JSON format with the above fields as keys. 
        If any field is not mentioned in the job description, return null for that field.
        """;


    private final ChatModel chatModel;


}
