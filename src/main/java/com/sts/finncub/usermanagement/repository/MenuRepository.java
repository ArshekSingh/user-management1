package com.sts.finncub.usermanagement.repository;

import com.sts.finncub.usermanagement.entity.Menu;
import com.sts.finncub.usermanagement.entity.MenuView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface MenuRepository extends JpaRepository<Menu, Long> {

    @Query(value ="SELECT MENU_ID, MENU_NAME, ACTION, LABEL, DISPLAY_SEQUENCE, PARENT_ID" +
            " FROM (" +
            "" +
            "    SELECT DISTINCT MM3.MENU_ID, MM3.MENU_NAME, MM3.ACTION, MM3.LABEL, MM3.DISPLAY_SEQUENCE, MM3.PARENT_ID, 1 LVL" +
            "" +
            "    FROM USER_MASTER U, USER_ROLE_MAPPING URM, MENU_ROLE_MAPPING MRM, MENU_MASTER MM, MENU_MASTER MM2, MENU_MASTER MM3" +
            "" +
            "    WHERE U.USER_ID = :userId" +
            "" +
            "    AND URM.ORG_ID = :orgId" +
            "" +
            "    AND U.USER_ID = URM.USER_ID" +
            "" +
            "    AND URM.ROLE_ID = MRM.ROLE_ID" +
            "" +
            "    AND MRM.MENU_ID = MM.MENU_ID" +
            "" +
            "    AND MM.PARENT_ID = MM2.MENU_ID" +
            "" +
            "    AND MM2.PARENT_ID = MM3.MENU_ID  UNION ALL" +
            "" +
            "    SELECT DISTINCT MM2.MENU_ID, MM2.MENU_NAME, MM2.ACTION, MM2.LABEL, MM2.DISPLAY_SEQUENCE, MM2.PARENT_ID, 2 LVL" +
            "" +
            "    FROM USER_MASTER U, USER_ROLE_MAPPING URM, MENU_ROLE_MAPPING MRM, MENU_MASTER MM, MENU_MASTER MM2" +
            "" +
            "    WHERE U.USER_ID = :userId" +
            "" +
            "    AND URM.ORG_ID = :orgId" +
            "" +
            "    AND U.USER_ID = URM.USER_ID" +
            "" +
            "    AND URM.ROLE_ID = MRM.ROLE_ID" +
            "" +
            "    AND MRM.MENU_ID = MM.MENU_ID" +
            "" +
            "    AND MM.PARENT_ID = MM2.MENU_ID" +
            "" +
            "    UNION ALL" +
            "" +
            "    SELECT MM.MENU_ID, MM.MENU_NAME, MM.ACTION, MM.LABEL, MM.DISPLAY_SEQUENCE, MM.PARENT_ID, 3 LVL" +
            "" +
            "    FROM USER_MASTER U, USER_ROLE_MAPPING URM, MENU_ROLE_MAPPING MRM, MENU_MASTER MM" +
            "" +
            "    WHERE U.USER_ID = :userId" +
            "" +
            "    AND URM.ORG_ID = :orgId" +
            "" +
            "    AND U.USER_ID = URM.USER_ID" +
            "" +
            "    AND URM.ROLE_ID = MRM.ROLE_ID" +
            "" +
            "    AND MRM.MENU_ID = MM.MENU_ID)",nativeQuery = true)
    List<MenuView> findMenuList(String userId, Long orgId);


}
