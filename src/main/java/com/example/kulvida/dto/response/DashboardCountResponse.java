package com.example.kulvida.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardCountResponse {

    List<Object[]> weeks;
    List<Object[]> months;
    List<Object[]> days;
 }
