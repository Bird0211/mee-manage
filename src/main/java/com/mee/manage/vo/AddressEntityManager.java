package com.mee.manage.vo;

import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class AddressEntityManager {

    @Transactional(value = "address", readOnly =false)
    protected void save() {
        // Some code here
    }

    @Transactional()
    public void find () {
        // Some code here
    }
    
}
