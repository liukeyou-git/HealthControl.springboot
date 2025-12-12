package com.example.web.enums;

import java.util.HashMap;

 /**
   *审核状态枚举
   */
public enum AuditStatusEnum 
  {
    /**
     * 待审核
     */    
    待审核(1), 
     
    /**
     * 审核通过
     */    
    审核通过(2), 
     
    /**
     * 审核失败
     */    
    审核失败(3); 
     
            
    private final int index;
    
    AuditStatusEnum(int index) 
    {
      this.index = index;
    }

    public int index() {
      return index;
    }
    public static AuditStatusEnum GetEnum(Integer v) {
        if (v == null) {
            return values()[0];
        }
        for (AuditStatusEnum myEnum : values()) {
            if (myEnum.index() == v) {
                return myEnum;
            }
        }
        return values()[0];
    }
      
     
 }
