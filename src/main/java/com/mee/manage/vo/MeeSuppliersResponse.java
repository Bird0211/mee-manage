package com.mee.manage.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Data
public class MeeSuppliersResponse {

    String result;

    String error;

    List<SuppliersVo> suppliers;

}
