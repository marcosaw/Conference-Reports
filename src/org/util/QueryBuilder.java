/*
 * El código contenido en este archivo, así como todos
 * los archivos compilados, son propiedad de
 * Marcos Avila Weingartshofer
 */
package org.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 *
 * @author Marcos Avila Weingartshofer
 */
public class QueryBuilder {

    private final Calendar dateStart;
    private final Calendar today = Calendar.getInstance();
    private final SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

    public QueryBuilder(int year, int month) {
        dateStart = new GregorianCalendar(year, month - 1, 1);
    }

    public String recursiveQuery1() {
        Calendar dateLowBound = (Calendar) dateStart.clone();
        StringBuilder query = new StringBuilder();
        while (dateLowBound.get(Calendar.MONTH) < today.get(Calendar.MONTH) || dateLowBound.get(Calendar.YEAR) < today.get(Calendar.YEAR)) {
            //while (dateLowBound.compareTo(today) <= 0) {
            query.append("round(sum(decode(to_char(starttime, 'MM/YYYY'), '");
            query.append(String.format("%02d", dateLowBound.get(Calendar.MONTH) + 1));
            query.append("/");
            query.append(dateLowBound.get(Calendar.YEAR));
            query.append("' ,DURATION_WEB,0)),2) ");
            query.append("\" ");
            query.append(dateLowBound.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH));
            query.append(" ");
            query.append(dateLowBound.get(Calendar.YEAR));
            query.append(" \",\n");
            dateLowBound.add(Calendar.MONTH, 1);
        }
        query.deleteCharAt(query.lastIndexOf(","));
        return query.toString();
    }

    public String recursiveQuery2() {
        Calendar dateLowBound = (Calendar) dateStart.clone();
        StringBuilder query = new StringBuilder();
        //while (dateLowBound.compareTo(today) <= 0) {
        while (dateLowBound.get(Calendar.MONTH) < today.get(Calendar.MONTH) || dateLowBound.get(Calendar.YEAR) < today.get(Calendar.YEAR)) {

            query.append("sum(decode(to_char(starttime, 'MM/YYYY'), '");
            query.append(String.format("%02d", dateLowBound.get(Calendar.MONTH) + 1));
            query.append("/");
            query.append(dateLowBound.get(Calendar.YEAR));
            query.append("' ,1,0)) ");
            query.append("\" ");
            query.append(dateLowBound.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.ENGLISH));
            query.append(" ");
            query.append(dateLowBound.get(Calendar.YEAR));
            query.append(" \",\n");
            dateLowBound.add(Calendar.MONTH, 1);
        }
        query.deleteCharAt(query.lastIndexOf(","));
        return query.toString();
    }

    //From jtagu.GenCAP_Report  where   trunc(starttime)>= to_date('2013/01/01','YYYY/MM/DD')  
    //group by countrycode order by    countrycode
    public String query1_1() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT countrycode AS Country,\n");
        query.append(recursiveQuery1());
        query.append("FROM jtagu.GenCAP_Report WHERE trunc(starttime)>= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD')\n");
        query.append("GROUP BY countrycode ORDER BY countrycode");
        return query.toString();
    }

    //From jtagu.GenCAP_Report
    //where   trunc(starttime)>= to_date('2013/01/01','YYYY/MM/DD') and duration_web != 0
    //group by  countrycode order by     countrycode 
    public String query1_2() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT countrycode, \n");
        query.append(recursiveQuery2());
        query.append("FROM jtagu.GenCAP_Report WHERE trunc(starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') AND duration_web != 0 \n");
        query.append("GROUP BY countrycode ORDER BY countrycode");
        return query.toString();
    }

//    From jtagu.GenCAP_Report a
//    where   trunc(a.starttime)>= to_date('2013/01/01','YYYY/MM/DD')
//    AND exists (select b.billingaccountid from genmc_db.baproperties b where b.billingaccountid = a.CONFEREENUMBER
//            and upper(b.propname) = upper('SiteType') 
//            and b.propvalue = '1')
//    group by countrycode order by countrycode  
    public String query2_1() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT a.countrycode, \n");
        query.append(recursiveQuery1());
        query.append("FROM jtagu.GenCAP_Report WHERE trunc(starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \nAND  (select b.billingaccountid FROM genmc_db.baproperties b"
                + " WHERE b.billingaccountid = a.CONFEREENUMBER"
                + " AND upper(b.propname) = upper('SiteType')"
                + " AND b.propvalue = '1')\n");
        query.append("GROUP BY countrycode ORDER BY countrycode");
        return query.toString();
    }

    public String query2_2() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT a.countrycode, \n");
        query.append(recursiveQuery2());
        query.append("FROM jtagu.GenCAP_Report WHERE trunc(starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') AND duration_web != 0"
                + " \nAND  (select b.billingaccountid "
                + " \n\tFROM genmc_db.baproperties b"
                + " \n\tWHERE b.billingaccountid = a.CONFEREENUMBER"
                + " \n\tAND upper(b.propname) = upper('SiteType')"
                + " \n\tAND b.propvalue = '1')\n");
        query.append("GROUP BY countrycode ORDER BY countrycode");
        return query.toString();
    }

    public String query3_1() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT countrycode AS Country,\n");
        query.append(recursiveQuery1());
        query.append("FROM jtagu.GenCAP_Report r WHERE trunc(starttime)>= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD')\n");
        query.append("AND r.version = '5' \n");
        query.append("GROUP BY countrycode ORDER BY countrycode");
        return query.toString();
    }

    public String query3_2() {
        StringBuilder query = new StringBuilder();
        query.append("Select countrycode, \n");
        query.append(recursiveQuery2());
        query.append("FROM jtagu.GenCAP_Report WHERE trunc(starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') AND duration_web != 0 \nAND version = '5' ");
        query.append("GROUP BY countrycode ORDER BY countrycode");
        return query.toString();
    }

    public String query4_1() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT countrycode AS Country,\n");
        query.append(recursiveQuery1());
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u \nWHERE trunc(starttime)>= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD')\n");
        query.append("AND r.version = '5' \n");
        query.append("AND r.CONFINSTID=C.WEB_ROOM_SESSION_ID and C.USER_ACCOUNT_ID=U.USER_ACCOUNT_ID \n");
        query.append("AND u.portal IN \n");
        query.append("\t(SELECT portal_id genmc5.portal WHERE portal_url IN (");
        query.append("\n\t\t'300000842.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000841.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000840.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000839.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000838.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000837.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000836.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000835.lobby.mc.iconf.net') ");
        query.append("\nGROUP BY countrycode ORDER BY countrycode");
        return query.toString();
    }

    public String query4_2() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT countrycode AS Country,\n");
        query.append(recursiveQuery2());
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u "
                + "\nWHERE trunc(starttime)>= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD')\n");
        query.append("AND r.version = '5' \n");
        query.append("AND r.CONFINSTID=C.WEB_ROOM_SESSION_ID and C.USER_ACCOUNT_ID=U.USER_ACCOUNT_ID \n");
        query.append("AND u.portal IN \n");
        query.append("\t(SELECT portal_id genmc5.portal WHERE portal_url IN (");
        query.append("\n\t\t'300000842.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000841.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000840.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000839.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000838.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000837.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000836.lobby.mc.iconf.net', ");
        query.append("\n\t\t'300000835.lobby.mc.iconf.net') ");
        query.append("\nGROUP BY countrycode ORDER BY countrycode");
        return query.toString();
    }

    // Reporte 2
    public String query1_1_() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ownerid, displayname, \n");
        query.append(recursiveQuery1());
        query.append("FROM ((SELECT u.ownerid ownerid, r.starttime starttime, r.duration_web, u.displayname displayname "
                + "FROM jtagu.GenCAP_Report r, GENMC_DB.CONFINSTANCES c, GENMC_DB.CONFSCHEDULES s, GENMC_DB.USERACCOUNTS u "
                + "WHERE trunc(r.starttime)>= ('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version is null "
                + "\nAND r.confinstid = c.confinstid "
                + "\nAND c.scheduleid = s.scheduleid "
                + "\nAND s.useraccountid = u.useraccountid "
                + "\nAND u.ownerid in ");
        query.append(UserlListDispatcher.getUserList()); // (8194887, 72... ...27,2838796)
        query.append(")\nunion all  \n(");
        query.append("\nSELECT u.ic_owner_id ownerid, r.starttime starttime, r.duration_web duration_web, u.display_name displayname ");
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u \n");
        query.append("WHERE trunc(r.starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '5' "
                + "\nAND r.confinstid = c.web_room_session_id "
                + "\nAND c.user_account_id = u.user_account_id "
                + "\nAND u.ic_owner_id in ");
        query.append(UserlListDispatcher.getUserList()); // (836234... )
        query.append(")\n) GROUP BY ownerid, displayname ORDER BY ownerid");
        return query.toString();
    }

    public String query1_2_() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ownerid, displayname, \n");
        query.append(recursiveQuery1());
        query.append("FROM (("
                + "SELECT u.ownerid ownerid, r.starttime starttime, r.duration_web, u.displayname displayname "
                + "FROM jtagu.GenCAP_Report r, GENMC_DB.CONFINSTANCES c, GENMC_DB.CONFSCHEDULES s, GENMC_DB.USERACCOUNTS u "
                + "WHERE trunc(r.starttime)>= ('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '0' "
                + "\nAND r.confinstid = c.confinstid "
                + "\nAND c.scheduleid = s.scheduleid "
                + "\nAND s.useraccountid = u.useraccountid "
                + "\nAND u.ownerid in ");
        query.append(UserlListDispatcher.getUserList()); // (8194887, 72... ...27,2838796)
        query.append(")\nunion all  \n(");
        query.append("\nSELECT u.ic_owner_id ownerid, r.starttime starttime, r.duration_web duration_web, u.display_name displayname ");
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u \n");
        query.append("WHERE trunc(r.starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '5' "
                + "\nAND r.confinstid = c.web_room_session_id "
                + "\nAND c.user_account_id = u.user_account_id "
                + "\nAND u.ic_owner_id in ");
        query.append(UserlListDispatcher.getUserList()); // (836234... )
        query.append(")\n) GROUP BY ownerid, displayname ORDER BY ownerid");
        return query.toString();
    }

    public String query1_3_() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ownerid, displayname, \n");
        query.append(recursiveQuery1());
        query.append("FROM (("
                + "SELECT u.ownerid ownerid, r.starttime starttime, r.duration_web, u.displayname displayname "
                + "FROM jtagu.GenCAP_Report r, GENMC_DB.CONFINSTANCES c, GENMC_DB.CONFSCHEDULES s, GENMC_DB.USERACCOUNTS u "
                + "WHERE trunc(r.starttime)>= ('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version IS NULL "
                + "\nAND exists(SELECT b.billingaccountid FROM genmc_db.baproperties b WHERE b.billingaccountid = r.confereenumber "
                + "\n\tAND upper(b.propname) = upper('SiteType')"
                + "\n\tAND b.propvalue = '1')"
                + "\nAND r.confinstid = c.confinstid "
                + "\nAND c.scheduleid = s.scheduleid "
                + "\nAND s.useraccountid = u.useraccountid "
                + "\nAND u.ownerid in ");
        query.append(UserlListDispatcher.getUserList()); // (8194887, 72... ...27,2838796)
        query.append(")\nunion all  \n(");
        query.append("\nSELECT u.ic_owner_id ownerid, r.starttime starttime, r.duration_web duration_web, u.display_name displayname ");
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u \n");
        query.append("WHERE trunc(r.starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '5' "
                + "\nAND exist (SELECT b.billingaccountid FROM genmc_db.baproperties b WHERE b.billingaccountid = r.confereenumber"
                + "\n\tAND upper(b.propname) = upper('SiteType')"
                + "\n\tAND b.propvalue = '1')"
                + "\nAND r.confinstid = c.web_room_session_id "
                + "\nAND c.user_account_id = u.user_account_id "
                + "\nAND u.ic_owner_id in ");
        query.append(UserlListDispatcher.getUserList()); // (836234... )
        query.append(")\n) GROUP BY ownerid, displayname ORDER BY ownerid");
        return query.toString();
    }

    public String query1_4_() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ownerid, displayname, \n");
        query.append(recursiveQuery1());
        query.append("FROM (("
                + "SELECT u.ownerid ownerid, r.starttime starttime, r.duration_web, u.displayname displayname "
                + "FROM jtagu.GenCAP_Report r, GENMC_DB.CONFINSTANCES c, GENMC_DB.CONFSCHEDULES s, GENMC_DB.USERACCOUNTS u "
                + "WHERE trunc(r.starttime)>= ('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = 'aa' "
                + "\nAND r.confinstid = c.confinstid "
                + "\nAND c.scheduleid = s.scheduleid "
                + "\nAND s.useraccountid = u.useraccountid "
                + "\nAND u.ownerid in ");
        query.append(UserlListDispatcher.getUserList()); // (8194887, 72... ...27,2838796)
        query.append(")\nunion all  \n(");
        query.append("\nSELECT u.ic_owner_id ownerid, r.starttime starttime, r.duration_web duration_web, u.display_name displayname ");
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u \n");
        query.append("WHERE trunc(r.starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '5' "
                + "\nAND u.portal_id IN(SELECT portal_id FROM genmc5.portal WHERE portal_URL IN (\n "
                + "'300000842.lobby.mc.iconf.net',\n"
                + "\t'300000841.lobby.mc.iconf.net',\n"
                + "\t'300000840.lobby.mc.iconf.net',\n"
                + "\t'300000839.lobby.mc.iconf.net',\n"
                + "\t'300000838.lobby.mc.iconf.net',\n"
                + "\t'300000837.lobby.mc.iconf.net',\n"
                + "\t'300000836.lobby.mc.iconf.net',\n"
                + "\t'300000835.lobby.mc.iconf.net')"
                + "\nAND r.confinstid = c.web_room_session_id "
                + "\nAND c.user_account_id = u.user_account_id "
                + "\nAND u.ic_owner_id in ");
        query.append(UserlListDispatcher.getUserList()); // (836234... )
        query.append("))\n) GROUP BY ownerid, displayname ORDER BY ownerid");
        return query.toString();
    }

    public String query2_1_() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ownerid, displayname, \n");
        query.append(recursiveQuery2());
        query.append("FROM (("
                + "SELECT u.ownerid ownerid, r.starttime starttime, r.duration_web, u.displayname displayname "
                + "FROM jtagu.GenCAP_Report r, GENMC_DB.CONFINSTANCES c, GENMC_DB.CONFSCHEDULES s, GENMC_DB.USERACCOUNTS u "
                + "WHERE trunc(r.starttime)>= ('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version is null AND r.duration_web != 0 "
                + "\nAND r.confinstid = c.confinstid "
                + "\nAND c.scheduleid = s.scheduleid "
                + "\nAND s.useraccountid = u.useraccountid "
                + "\nAND u.ownerid in ");
        query.append(UserlListDispatcher.getUserList()); // (8194887, 72... ...27,2838796)
        query.append(")\nunion all  \n(");
        query.append("\nSELECT u.ic_owner_id ownerid, r.starttime starttime, r.duration_web duration_web, u.display_name displayname ");
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u \n");
        query.append("WHERE trunc(r.starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '5' AND r.duration_web != 0 "
                + "\nAND r.confinstid = c.web_room_session_id "
                + "\nAND c.user_account_id = u.user_account_id "
                + "\nAND u.ic_owner_id in ");
        query.append(UserlListDispatcher.getUserList()); // (836234... )
        query.append(")\n) GROUP BY ownerid, displayname ORDER BY ownerid");
        return query.toString();
    }

    public String query2_2_() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ownerid, displayname, \n");
        query.append(recursiveQuery2());
        query.append("FROM (("
                + "SELECT u.ownerid ownerid, r.starttime starttime, r.duration_web, u.displayname displayname "
                + "FROM jtagu.GenCAP_Report r, GENMC_DB.CONFINSTANCES c, GENMC_DB.CONFSCHEDULES s, GENMC_DB.USERACCOUNTS u "
                + "WHERE trunc(r.starttime)>= ('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '0' AND r.duration_web != 0 "
                + "\nAND r.confinstid = c.confinstid "
                + "\nAND c.scheduleid = s.scheduleid "
                + "\nAND s.useraccountid = u.useraccountid "
                + "\nAND u.ownerid in ");
        query.append(UserlListDispatcher.getUserList()); // (8194887, 72... ...27,2838796)
        query.append(")\nunion all  \n(");
        query.append("\nSELECT u.ic_owner_id ownerid, r.starttime starttime, r.duration_web duration_web, u.display_name displayname ");
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u \n");
        query.append("WHERE trunc(r.starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '5' AND r.duration_web != 0 "
                + "\nAND r.confinstid = c.web_room_session_id "
                + "\nAND c.user_account_id = u.user_account_id "
                + "\nAND u.ic_owner_id in ");
        query.append(UserlListDispatcher.getUserList()); // (836234... )
        query.append(")\n) GROUP BY ownerid, displayname ORDER BY ownerid");
        return query.toString();
    }

    public String query2_3_() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ownerid, displayname, \n");
        query.append(recursiveQuery2());
        query.append("FROM (("
                + "SELECT u.ownerid ownerid, r.starttime starttime, r.duration_web, u.displayname displayname "
                + "FROM jtagu.GenCAP_Report r, GENMC_DB.CONFINSTANCES c, GENMC_DB.CONFSCHEDULES s, GENMC_DB.USERACCOUNTS u "
                + "WHERE trunc(r.starttime)>= ('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = is null AND r.duration_web != 0 "
                + "\n\tAND exists(SELECT b.billingaccountid FROM genmc_db.baproperties b WHERE b.billingaccountid = r.confereenumber "
                + "\n\tAND upper(b.propname) = upper('SiteType')"
                + "\n\tAND b.propvalue = '1')"
                + "\nAND r.confinstid = c.confinstid "
                + "\nAND c.scheduleid = s.scheduleid "
                + "\nAND s.useraccountid = u.useraccountid "
                + "\nAND u.ownerid in ");
        query.append(UserlListDispatcher.getUserList()); // (8194887, 72... ...27,2838796)
        query.append(")\nunion all  \n(");
        query.append("\nSELECT u.ic_owner_id ownerid, r.starttime starttime, r.duration_web duration_web, u.display_name displayname ");
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u \n");
        query.append("WHERE trunc(r.starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '5' AND r.duration_web != 0 "
                + "AND exists(SELECT b.billingaccountid FROM genmc_db.baproperties b WHERE b.billingaccountid = r.confereenumber "
                + "\n\tAND upper(b.propname) = upper('SiteType') "
                + "\n\tAND b.propvalue = '1') "
                + "\nAND r.confinstid = c.web_room_session_id "
                + "\nAND c.user_account_id = u.user_account_id "
                + "\nAND u.ic_owner_id in ");
        query.append(UserlListDispatcher.getUserList()); // (836234... )
        query.append(")\n) GROUP BY ownerid, displayname ORDER BY ownerid");
        return query.toString();
    }
    
    public String query2_4_() {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ownerid, displayname, \n");
        query.append(recursiveQuery2());
        query.append("FROM (("
                + "SELECT u.ownerid ownerid, r.starttime starttime, r.duration_web, u.displayname displayname "
                + "FROM jtagu.GenCAP_Report r, GENMC_DB.CONFINSTANCES c, GENMC_DB.CONFSCHEDULES s, GENMC_DB.USERACCOUNTS u "
                + "WHERE trunc(r.starttime)>= ('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = 'aa' and r.duration_web != 0"
                + "\nAND r.confinstid = c.confinstid "
                + "\nAND c.scheduleid = s.scheduleid "
                + "\nAND s.useraccountid = u.useraccountid "
                + "\nAND u.ownerid in ");
        query.append(UserlListDispatcher.getUserList()); // (8194887, 72... ...27,2838796)
        query.append(")\nunion all  \n(");
        query.append("\nSELECT u.ic_owner_id ownerid, r.starttime starttime, r.duration_web duration_web, u.display_name displayname ");
        query.append("FROM jtagu.GenCAP_Report r, REPORT.WEB_ROOM_SESSION_HISTORY c, GENMC5.USER_ACCOUNT u \n");
        query.append("WHERE trunc(r.starttime) >= to_date('");
        query.append(formatter.format(dateStart.getTime()));
        query.append("','YYYY/MM/DD') \n");
        query.append("AND r.version = '5' AND r.duration_web != 0 "
                + "\nAND u.portal_id IN(SELECT portal_id FROM genmc5.portal WHERE portal_URL IN (\n "
                + "'300000842.lobby.mc.iconf.net',\n"
                + "\t'300000841.lobby.mc.iconf.net',\n"
                + "\t'300000840.lobby.mc.iconf.net',\n"
                + "\t'300000839.lobby.mc.iconf.net',\n"
                + "\t'300000838.lobby.mc.iconf.net',\n"
                + "\t'300000837.lobby.mc.iconf.net',\n"
                + "\t'300000836.lobby.mc.iconf.net',\n"
                + "\t'300000835.lobby.mc.iconf.net')) "
                + "\nAND r.confinstid = c.web_room_session_id "
                + "\nAND c.user_account_id = u.user_account_id "
                + "\nAND u.ic_owner_id in ");
        query.append(UserlListDispatcher.getUserList()); // (836234... )
        query.append(")\n) GROUP BY ownerid, displayname ORDER BY ownerid");
        return query.toString();
    }
    
    public String mysqlQuery() {
        return "select * from usuario";
    }

    public static void main(String[] args) {
        QueryBuilder qb = new QueryBuilder(2013, 5);
        System.out.println("\n1_1_ ->\n");
        System.out.println(qb.query1_1_());
        System.out.println("\n2_1_ ->\n");
        System.out.println(qb.query2_1_());
        System.out.println("\n1_2_ ->\n");
        System.out.println(qb.query1_2_());
        System.out.println("\n2_2_ ->\n");
        System.out.println(qb.query2_2_());
        System.out.println("\n1_3_ ->\n");
        System.out.println(qb.query1_3_());
        System.out.println("\n2_3_ ->\n");
        System.out.println(qb.query2_3_());
        System.out.println("\n1_4_ ->\n");
        System.out.println(qb.query1_4_());
        System.out.println("\n2_4_ ->\n");
        System.out.println(qb.query2_4_());
    }
}
