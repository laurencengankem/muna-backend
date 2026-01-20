package com.example.kulvida.repository;

import com.example.kulvida.entity.UserOrder;
import com.example.kulvida.entity.cloth.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DashboardRepository extends JpaRepository<Order,String> {

    @Query(value =
            "WITH RECURSIVE weeks AS ( " +
                    "    SELECT DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY) - INTERVAL 60 DAY AS week_start " +
                    "    UNION ALL " +
                    "    SELECT week_start + INTERVAL 7 DAY " +
                    "    FROM weeks " +
                    "    WHERE week_start + INTERVAL 7 DAY <= CURDATE() " +
                    ") " +
                    "SELECT w.week_start, " +
                    "       w.week_start + INTERVAL 6 DAY AS week_end, " +
                    "       SUM(uo.total), "+
                    "       COUNT(uo.order_date) AS order_count " +
                    "FROM weeks w " +
                    "LEFT JOIN user_order uo " +
                    "    ON DATE(uo.order_date) BETWEEN w.week_start AND w.week_start + INTERVAL 6 DAY " +
                    "GROUP BY w.week_start, week_end " +
                    "ORDER BY w.week_start ",
            nativeQuery = true)
    List<Object[]> getSalesByWeek();

    @Query(value =
            "WITH RECURSIVE months AS (" +
                    "    SELECT DATE_SUB(CURDATE(), INTERVAL 12 MONTH) AS month_start" +
                    "    UNION ALL" +
                    "    SELECT month_start + INTERVAL 1 MONTH" +
                    "    FROM months" +
                    "    WHERE month_start + INTERVAL 1 MONTH <= CURDATE()" +
                    ") " +
                    "SELECT " +
                    "    YEAR(w.month_start) AS year, " +
                    "    MONTH(w.month_start) AS month, " +
                    "    SUM(uo.total), "+
                    "    COUNT(uo.order_date) AS order_count " +
                    "FROM months w " +
                    "LEFT JOIN user_order uo " +
                    "    ON YEAR(uo.order_date) = YEAR(w.month_start) " +
                    "    AND MONTH(uo.order_date) = MONTH(w.month_start) " +
                    "GROUP BY YEAR(w.month_start), MONTH(w.month_start) " +
                    "ORDER BY YEAR(w.month_start), MONTH(w.month_start)",
            nativeQuery = true)
    List<Object[]> findOrderCountByMonth();


    @Query(value =
            "WITH RECURSIVE last_31_days AS (" +
                    "    SELECT CURDATE() - INTERVAL 31 DAY AS day_start" +
                    "    UNION ALL" +
                    "    SELECT day_start + INTERVAL 1 DAY" +
                    "    FROM last_31_days" +
                    "    WHERE day_start + INTERVAL 1 DAY <= CURDATE()" +
                    ") " +
                    "SELECT " +
                    "    DATE(w.day_start) AS date, " +  // Date format
                    "    SUM(uo.total), "+
                    "    COUNT(uo.order_date) AS order_count " +
                    "FROM last_31_days w " +
                    "LEFT JOIN user_order uo " +
                    "    ON DATE(uo.order_date) = DATE(w.day_start) " +
                    "GROUP BY DATE(w.day_start) " +
                    "ORDER BY DATE(w.day_start) DESC",
            nativeQuery = true)
    List<Object[]> findOrderCountByDay();





}
