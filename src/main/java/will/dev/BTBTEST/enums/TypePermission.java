package will.dev.BTBTEST.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum TypePermission {
    ADMIN_CREATE,
    ADMIN_READ,
    ADMIN_UPDATE,
    ADMIN_DELETE,

    MANAGER_CREATE,
    MANAGER_READ,
    MANAGER_UPDATE,
    MANAGER_DELETE,

    USER_CREATE,
    USER_READ;

}
