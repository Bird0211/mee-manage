package com.mee.manage.vo;

import lombok.Data;
import java.util.List;

@Data
public class MeeSuppliersResponse {

    String result;

    String error;

    List<SuppliersVo> suppliers;

}
