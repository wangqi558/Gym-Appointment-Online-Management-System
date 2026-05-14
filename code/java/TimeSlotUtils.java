import javax.swing.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for time slot management
 */
public class TimeSlotUtils {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Get available start time options
     */
    public static String[] getStartTimeOptions() {
        return generateTimeSlots("06:00", "22:00"); // Default hours
    }

    /**
     * Get available end time options based on start time
     */
    public static String[] getEndTimeOptions(String startTime) {
        if (startTime == null || startTime.isEmpty()) {
            return generateTimeSlots("06:00", "22:00"); // Default hours
        }

        List<String> endTimes = new ArrayList<>();
        boolean foundStart = false;
        String[] baseSlots = generateTimeSlots("06:00", "22:00"); // Default hours

        for (String slot : baseSlots) {
            if (foundStart) {
                endTimes.add(slot);
            }
            if (slot.equals(startTime)) {
                foundStart = true;
            }
        }

        return endTimes.toArray(new String[0]);
    }

    /**
     * Create time combo box with start times
     */
    public static JComboBox<String> createStartTimeComboBox() {
        JComboBox<String> comboBox = new JComboBox<>(getStartTimeOptions());
        comboBox.setSelectedIndex(4); // Default to 09:00
        return comboBox;
    }

    /**
     * Create time combo box with end times
     */
    public static JComboBox<String> createEndTimeComboBox(String startTime) {
        JComboBox<String> comboBox = new JComboBox<>(getEndTimeOptions(startTime));
        if (comboBox.getItemCount() > 0) {
            comboBox.setSelectedIndex(0); // Select first available end time
        }
        return comboBox;
    }

    /**
     * Calculate duration in hours between two times
     */
    public static double calculateDurationHours(String startTime, String endTime) {
        try {
            LocalTime start = LocalTime.parse(startTime, TIME_FORMATTER);
            LocalTime end = LocalTime.parse(endTime, TIME_FORMATTER);

            // Handle overnight bookings
            if (end.isBefore(start)) {
                end = end.plusHours(24);
            }

            return Duration.between(start, end).toHours();
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Validate time selection
     */
    public static boolean validateTimeSelection(String startTime, String endTime) {
        if (startTime == null || endTime == null || startTime.isEmpty() || endTime.isEmpty()) {
            return false;
        }

        double duration = calculateDurationHours(startTime, endTime);
        return duration > 0 && duration <= 2; // Max 2 hours
    }

    /**
     * Get error message for invalid time selection
     */
    public static String getTimeValidationError(String startTime, String endTime) {
        if (!validateTimeSelection(startTime, endTime)) {
            double duration = calculateDurationHours(startTime, endTime);
            if (duration <= 0) {
                return "结束时间必须晚于开始时间！";
            } else if (duration > 2) {
                return "预订时间不能超过2小时！";
            }
        }
        return null;
    }

    /**
     * Format full date time string
     */
    public static String formatDateTime(String date, String time) {
        return date + " " + time;
    }

    /**
     * Get current date in yyyy-MM-dd format
     */
    public static String getCurrentDate() {
        return LocalDate.now().toString();
    }

    /**
     * Calculate price based on duration and base price
     */
    public static double calculatePrice(double basePrice, double durationHours) {
        return basePrice * durationHours;
    }

    /**
     * Generate time slots based on gym open and close times
     */
    public static String[] generateTimeSlots(String openTime, String closeTime) {
        return generateTimeSlots(openTime, closeTime, 60); // Default to 60-minute intervals
    }

    /**
     * Generate time slots between open and close time with custom interval
     */
    public static String[] generateTimeSlots(String openTime, String closeTime, int intervalMinutes) {
        List<String> slots = new ArrayList<>();

        try {
            LocalTime open = LocalTime.parse(openTime, TIME_FORMATTER);
            LocalTime close = LocalTime.parse(closeTime, TIME_FORMATTER);

            // Generate slots with specified interval
            LocalTime current = open;
            while (current.isBefore(close)) {
                slots.add(current.format(TIME_FORMATTER));
                current = current.plusMinutes(intervalMinutes);
            }
        } catch (Exception e) {
            // Default slots if parsing fails - 60-minute intervals
            return new String[]{
                "09:00", "10:00", "11:00", "12:00",
                "13:00", "14:00", "15:00", "16:00",
                "17:00", "18:00", "19:00", "20:00", "21:00"
            };
        }

        return slots.toArray(new String[0]);
    }

    /**
     * Get available start time options for a specific gym
     */
    public static String[] getStartTimeOptions(String openTime, String closeTime) {
        return generateTimeSlots(openTime, closeTime);
    }

    /**
     * Get available end time options based on start time and gym close time
     */
    public static String[] getEndTimeOptions(String startTime, String closeTime) {
        if (startTime == null || startTime.isEmpty() || closeTime == null || closeTime.isEmpty()) {
            return new String[0];
        }

        List<String> endTimes = new ArrayList<>();

        try {
            LocalTime start = LocalTime.parse(startTime, TIME_FORMATTER);
            LocalTime close = LocalTime.parse(closeTime, TIME_FORMATTER);

            // Generate  1 hour intervals from start time + 1 hour to close time
            LocalTime current = start.plusHours(1); // End time can be  1 hour after start
            while (current.isBefore(close) || current.equals(close)) {
                endTimes.add(current.format(TIME_FORMATTER));
                current = current.plusHours(1);//  1 hour intervals
            }
        } catch (Exception e) {
            return new String[0];
        }

        return endTimes.toArray(new String[0]);
    }

    /**
     * Create time combo box with gym-specific hours
     */
    public static JComboBox<String> createStartTimeComboBox(String openTime, String closeTime) {
        JComboBox<String> comboBox = new JComboBox<>(getStartTimeOptions(openTime, closeTime));
        if (comboBox.getItemCount() > 0) {
            comboBox.setSelectedIndex(0); // Select first available time
        }
        return comboBox;
    }

    /**
     * Create end time combo box based on start time and gym hours
     */
    public static JComboBox<String> createEndTimeComboBox(String startTime, String closeTime) {
        JComboBox<String> comboBox = new JComboBox<>(getEndTimeOptions(startTime, closeTime));
        if (comboBox.getItemCount() > 0) {
            comboBox.setSelectedIndex(0); // Select first available end time
        }
        return comboBox;
    }

    /**
     * Validate time selection against gym hours
     */
    public static String validateTimeSelection(String startTime, String endTime, String openTime, String closeTime) {
        if (startTime == null || endTime == null || openTime == null || closeTime == null) {
            return "时间选择不能为空！";
        }

        try {
            LocalTime start = LocalTime.parse(startTime, TIME_FORMATTER);
            LocalTime end = LocalTime.parse(endTime, TIME_FORMATTER);
            LocalTime open = LocalTime.parse(openTime, TIME_FORMATTER);
            LocalTime close = LocalTime.parse(closeTime, TIME_FORMATTER);

            // Check if start time is before open time
            if (start.isBefore(open)) {
                return "开始时间不能早于体育馆开放时间！";
            }

            // Check if end time is after close time
            if (end.isAfter(close)) {
                return "结束时间不能晚于体育馆关闭时间！";
            }

            // Check if start time is after end time
            if (start.isAfter(end) || start.equals(end)) {
                return "结束时间必须晚于开始时间！";
            }

            // Check duration
            double duration = calculateDurationHours(startTime, endTime);
            if (duration > 2) {
                return "预订时间不能超过2小时！";
            }

            return null; // Valid
        } catch (Exception e) {
            return "时间格式错误！";
        }
    }
}