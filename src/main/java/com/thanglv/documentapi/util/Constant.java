package com.thanglv.documentapi.util;

public interface Constant {
    public interface FILE_RESOURCE {
        String TYPE = "file";

        String SCOPE_VIEW = "file:view";
        String SCOPE_CREATE = "file:create";
        String SCOPE_DELETE= "file:delete";
    }

    interface ERR_CODE {
        String SUCCESS = "200";
        String ERR_400 = "400";
        String ERR_403 = "403";
        String ERR_404 = "404";
    }
    String STR_N = "N";
    String STR_Y = "Y";
    String ACTIVE = "ACTIVE";
    String INACTIVE = "INACTIVE";
    interface ROLE {
        String ROLE_ADMIN = "ADMIN";
        String ROLE_USER = "USER";
    }
}
