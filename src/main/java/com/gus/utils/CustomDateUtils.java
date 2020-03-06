package com.gus.utils;

import com.gus.bean.DateZoneResult;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description 日期处理工具，将日期处理为按周、月、季度拆分时间段
 * @Author GusChan
 * @Date 2020/3/6 17:37
 * @Version 1.0
 */
public class CustomDateUtils {


    public static void main(String[] args) {
        LocalDate start = LocalDate.parse("2019-02-12");
        LocalDate end = LocalDate.parse("2020-11-15");
//        List<DateZoneResult> list = getWeeks(start, end);
//        List<DateZoneResult> list = getMonths(start, end);
        List<DateZoneResult> list = getQuarter(start, end);
        list.forEach(s -> System.out.println(s));
    }

    /**
     * 根据时间段拆分为周
     *
     * @param startDate 开始日期，必传
     * @param endDate   结束日期，不传时取当前系统日期
     * @return
     */
    public static List<DateZoneResult> getWeeks(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return null;
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        List<DateZoneResult> list = new ArrayList<>();
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        // 是否为同一周的判断
        if (startDate.get(weekFields.weekOfWeekBasedYear()) == endDate.get(weekFields.weekOfWeekBasedYear())) {
            DateZoneResult zoneResult = getNormalZoneResult(startDate, endDate);
            list.add(zoneResult);
        } else {
            // 调整期分别获取周数中的第一天与最后一天
            TemporalAdjuster FIRST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(
                    localDate -> localDate.minusDays(localDate.getDayOfWeek().getValue() - DayOfWeek.MONDAY.getValue()));
            TemporalAdjuster LAST_OF_WEEK = TemporalAdjusters.ofDateAdjuster(
                    localDate -> localDate.plusDays(DayOfWeek.SUNDAY.getValue() - localDate.getDayOfWeek().getValue()));
            // 开始周开始日期与结束日期
            LocalDate startFirstWeek = startDate.with(FIRST_OF_WEEK);
            LocalDate endFirstWeek = startDate.with(LAST_OF_WEEK);
            // 结束周开始日期与结束日期
            LocalDate startLastWeek = endDate.with(FIRST_OF_WEEK);
            LocalDate endLastWeek = endDate.with(LAST_OF_WEEK);
            LocalDate tempDate = startFirstWeek;
            while (endLastWeek.compareTo(tempDate) >= 0) {
                DateZoneResult zoneResult = new DateZoneResult();
                if (tempDate.equals(startFirstWeek)) {
                    // 第一周
                    zoneResult.setStartDate(startDate).setEndDate(endFirstWeek);
                    String startDateStr = startDate.format(getDtf());
                    String endDateStr = endFirstWeek.format(getDtf());
                    zoneResult.setDateZone(startDateStr + "-" + endDateStr);
                } else if (endLastWeek.equals(tempDate)) {
                    // 此为最后一周
                    zoneResult.setStartDate(startLastWeek).setEndDate(endDate);
                    String startDateStr = startLastWeek.format(getDtf());
                    String endDateStr = endDate.format(getDtf());
                    zoneResult.setDateZone(startDateStr + "-" + endDateStr);
                } else {
                    LocalDate tempStart = tempDate.with(FIRST_OF_WEEK);
                    LocalDate tempEnd = tempDate.with(LAST_OF_WEEK);
                    zoneResult.setStartDate(tempStart).setEndDate(tempEnd);
                    zoneResult.setDateZone(tempStart.format(getDtf()) + "-" + tempEnd.format(getDtf()));
                }
                list.add(zoneResult);
                tempDate = tempDate.plusDays(7);
            }
        }
        return list;
    }

    /**
     * 根据时间段拆分为月
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<DateZoneResult> getMonths(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return null;
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        List<DateZoneResult> list = new ArrayList<>();
        // 判断是否同一年的同一个月
        if (startDate.getYear() == endDate.getYear() && startDate.getMonthValue() == endDate.getMonthValue()) {
            DateZoneResult zoneResult = getNormalZoneResult(startDate, endDate);
            list.add(zoneResult);
        } else {
            // 调整期分别获取月份的第一天和月份的最后一天
            TemporalAdjuster FIRST_DAY_MONTH = TemporalAdjusters.firstDayOfMonth();
            TemporalAdjuster LAST_DAY_MONTH = TemporalAdjusters.lastDayOfMonth();
            LocalDate tempDate = startDate.with(FIRST_DAY_MONTH);
            while (endDate.with(FIRST_DAY_MONTH).compareTo(tempDate) >= 0) {
                DateZoneResult zoneResult = new DateZoneResult();
                if (tempDate.equals(startDate.with(FIRST_DAY_MONTH))) {
                    // 开始月
                    zoneResult.setStartDate(startDate).setEndDate(startDate.with(LAST_DAY_MONTH));
                    String startDateStr = startDate.format(getDtf());
                    String endDateStr = startDate.with(LAST_DAY_MONTH).format(getDtf());
                    zoneResult.setDateZone(startDateStr + "-" + endDateStr);
                } else if (tempDate.equals(endDate.with(FIRST_DAY_MONTH))) {
                    // 此为最后月份
                    zoneResult.setStartDate(endDate.with(FIRST_DAY_MONTH)).setEndDate(endDate);
                    String startDateStr = endDate.with(FIRST_DAY_MONTH).format(getDtf());
                    String endDateStr = endDate.format(getDtf());
                    zoneResult.setDateZone(startDateStr + "-" + endDateStr);
                } else {
                    LocalDate tempStart = tempDate.with(FIRST_DAY_MONTH);
                    LocalDate tempEnd = tempDate.with(LAST_DAY_MONTH);
                    zoneResult.setStartDate(tempStart).setEndDate(tempEnd);
                    zoneResult.setDateZone(tempStart.format(getDtf()) + "-" + tempEnd.format(getDtf()));
                }
                list.add(zoneResult);
                tempDate = tempDate.plusMonths(1);
            }
        }
        return list;
    }

    /**
     * 根据时间段拆分季度
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static List<DateZoneResult> getQuarter(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return null;
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        List<DateZoneResult> list = new ArrayList<>();
        // 判断是否同一年的同一个季度
        if (startDate.getYear() == endDate.getYear() && getQuarterFirstDay(startDate).equals(getQuarterFirstDay(endDate))) {
            DateZoneResult zoneResult = getNormalZoneResult(startDate, endDate);
            list.add(zoneResult);
        } else {
            // 调整期分别获取月份的第一天和月份的最后一天
            TemporalAdjuster FIRST_DAY_MONTH = TemporalAdjusters.firstDayOfMonth();
            TemporalAdjuster LAST_DAY_MONTH = TemporalAdjusters.lastDayOfMonth();
            // 获取最后时间的当前季度的第一天
            LocalDate endQuarterFirstDay = getQuarterFirstDay(endDate);
            LocalDate tempDate = getQuarterFirstDay(startDate);
            while (endQuarterFirstDay.compareTo(tempDate) >= 0) {
                DateZoneResult zoneResult = new DateZoneResult();
                if (tempDate.equals(getQuarterFirstDay(startDate))) {
                    // 开始季度
                    zoneResult.setStartDate(startDate).setEndDate(getQuarterLastDay(startDate));
                    String startDateStr = startDate.format(getDtf());
                    String endDateStr = getQuarterLastDay(startDate).format(getDtf());
                    zoneResult.setDateZone(startDateStr + "-" + endDateStr);
                } else if (endQuarterFirstDay.equals(tempDate)) {
                    // 结束季度
                    zoneResult.setStartDate(endQuarterFirstDay).setEndDate(endDate);
                    String startDateStr = endQuarterFirstDay.format(getDtf());
                    String endDateStr = endDate.format(getDtf());
                    zoneResult.setDateZone(startDateStr + "-" + endDateStr);
                } else {
                    LocalDate tempStart = getQuarterFirstDay(tempDate);
                    LocalDate tempEnd = getQuarterLastDay(tempDate);
                    zoneResult.setStartDate(tempStart).setEndDate(tempEnd);
                    zoneResult.setDateZone(tempStart.format(getDtf()) + "-" + tempEnd.format(getDtf()));
                }
                list.add(zoneResult);
                tempDate = tempDate.plusMonths(3);
            }
        }
        return list;
    }

    /**
     * 获取当前时间所属季度的第一天
     *
     * @param localDate
     * @return
     */
    private static LocalDate getQuarterFirstDay(LocalDate localDate) {
        LocalDate date = null;
        int year = localDate.getYear();
        int monthValue = localDate.getMonthValue();
        if (monthValue >= 10) {
            date = LocalDate.of(year, 10, 1);
        } else if (monthValue >= 7) {
            date = LocalDate.of(year, 7, 1);
        } else if (monthValue >= 4) {
            date = LocalDate.of(year, 4, 1);
        } else {
            date = LocalDate.of(year, 1, 1);
        }
        return date;
    }

    /**
     * 获取当前时间的所属季度最后一天
     *
     * @param localDate
     * @return
     */
    private static LocalDate getQuarterLastDay(LocalDate localDate) {
        LocalDate date = null;
        int year = localDate.getYear();
        int monthValue = localDate.getMonthValue();
        if (monthValue >= 10) {
            date = LocalDate.of(year, 12, 31);
        } else if (monthValue >= 7) {
            date = LocalDate.of(year, 9, 30);
        } else if (monthValue >= 4) {
            date = LocalDate.of(year, 6, 30);
        } else {
            date = LocalDate.of(year, 3, 31);
        }
        return date;
    }

    private static DateZoneResult getNormalZoneResult(LocalDate startDate, LocalDate endDate) {
        DateZoneResult zoneResult = new DateZoneResult();
        zoneResult.setStartDate(startDate).setEndDate(endDate);
        String startDateStr = startDate.format(getDtf());
        String endDateStr = endDate.format(getDtf());
        zoneResult.setDateZone(startDateStr + "-" + endDateStr);
        return zoneResult;
    }

    private static DateTimeFormatter getDtf() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return dtf;
    }

}
