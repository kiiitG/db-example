import java.time.LocalDateTime;
import java.util.List;

public class Assertions {
    public static String assertArgsCountBigger(String[] args, int count) {
        if (args.length <= count) {
            return "Wrong command argument.";
        }
        return "";
    }

    public static String assertArgsCount(String[] args, int count) {
        if (args.length != count) {
            return "Wrong command argument.";
        }
        return "";
    }

    public static String assertLong(String[] args, List<Integer> longArgs) {
        for (int longArg : longArgs) {
            try {
                Long.parseLong(args[longArg]);
            } catch (NumberFormatException e) {
                return e.getMessage();
            }
        }
        return "";
    }

    public static String assertByte(String[] args, List<Integer> byteArgs) {
        for (int byteArg : byteArgs) {
            try {
                Byte.parseByte(args[byteArg]);
            } catch (NumberFormatException e) {
                return e.getMessage();
            }
        }
        return "";
    }

    public static String assertDateTime(String[] args, List<Integer> byteArgs) {
        for (int byteArg : byteArgs) {
            try {
                LocalDateTime.parse(args[byteArg], Utils.FORMATTER);
            } catch (NumberFormatException e) {
                return e.getMessage();
            }
        }
        return "";
    }

    public static String assertValidTimeGap(String start, String end) {
        try {
            if (!LocalDateTime.parse(start, Utils.FORMATTER).isBefore(LocalDateTime.parse(end, Utils.FORMATTER))) {
                return "Invalid time gap.";
            }
        } catch (NumberFormatException e) {
            return "";
        }
        return "";
    }
}
