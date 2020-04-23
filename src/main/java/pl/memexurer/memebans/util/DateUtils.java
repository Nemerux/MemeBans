package pl.memexurer.memebans.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DateUtils {
    private static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("dd-MM-yyyy, HH:mm:ss");

    public static String formatFullTime(long millis) {
        StringBuilder string = new StringBuilder();
        int days = (int) ((millis / (86400000)));
        int secs = (int) (millis / 1000) % 60;
        int minutes = (int) ((millis / (1000 * 60)) % 60);
        int hour = (int) ((millis / (1000 * 60 * 60)) % 24);
        if (days != 0) {
            if (days == 1) {
                string.append("1 dzień");
            } else if (days > 1) {
                string.append(days).append(" dni");
            }
            if (hour != 0) string.append(", ");
        }

        if (hour != 0) {
            if (hour == 1) {
                string.append("1 godzina");
            } else if (hour < 4) {
                string.append(hour).append(" godziny");
            } else if (minutes > 4) {
                string.append(hour).append(" godzin");
            }
            if (minutes != 0) string.append(", ");
        }
        if (minutes != 0) {
            if (minutes == 1) {
                string.append("1 minuta");
            } else if (minutes < 4) {
                string.append(minutes).append(" minuty");
            } else if (minutes > 4) {
                string.append(minutes).append(" minut");
            }
            if (secs != 0) string.append(" i ");
        }
        if (secs != 0) {
            if (secs == 1) {
                string.append("1 sekunda");
            } else if (secs < 4) {
                string.append(secs).append(" sekundy");
            } else if (secs > 4) {
                string.append(secs).append(" sekund");
            }
        }
        return string.toString();
    }

    public static long parseDateDiff(String time, boolean future) {
        if (time == null) return 0;
        try {
            Pattern timePattern = Pattern.compile("(?:([0-9]+)\\s*y[a-z]*[,\\s]*)?(?:([0-9]+)\\s*mo[a-z]*[,\\s]*)?(?:([0-9]+)\\s*w[a-z]*[,\\s]*)?(?:([0-9]+)\\s*d[a-z]*[,\\s]*)?(?:([0-9]+)\\s*h[a-z]*[,\\s]*)?(?:([0-9]+)\\s*m[a-z]*[,\\s]*)?(?:([0-9]+)\\s*(?:s[a-z]*)?)?", 2);
            Matcher m = timePattern.matcher(time);
            int years = 0;
            int months = 0;
            int weeks = 0;
            int days = 0;
            int hours = 0;
            int minutes = 0;
            int seconds = 0;
            boolean found = false;
            while (m.find()) {
                if ((m.group() != null) && (!m.group().isEmpty())) {
                    for (int i = 0; i < m.groupCount(); i++) {
                        if ((m.group(i) != null) && (!m.group(i).isEmpty())) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        if ((m.group(1) != null) && (!m.group(1).isEmpty())) {
                            years = Integer.parseInt(m.group(1));
                        }
                        if ((m.group(2) != null) && (!m.group(2).isEmpty())) {
                            months = Integer.parseInt(m.group(2));
                        }
                        if ((m.group(3) != null) && (!m.group(3).isEmpty())) {
                            weeks = Integer.parseInt(m.group(3));
                        }
                        if ((m.group(4) != null) && (!m.group(4).isEmpty())) {
                            days = Integer.parseInt(m.group(4));
                        }
                        if ((m.group(5) != null) && (!m.group(5).isEmpty())) {
                            hours = Integer.parseInt(m.group(5));
                        }
                        if ((m.group(6) != null) && (!m.group(6).isEmpty())) {
                            minutes = Integer.parseInt(m.group(6));
                        }
                        if ((m.group(7) != null) && (!m.group(7).isEmpty())) {
                            seconds = Integer.parseInt(m.group(7));
                        }
                    }
                }
            }
            if (!found) {
                return 0L;
            }

            Calendar c = new GregorianCalendar();
            if (years > 0) {
                c.add(Calendar.YEAR, years * (future ? 1 : -1));
            }
            if (months > 0) {
                c.add(Calendar.MONTH, months * (future ? 1 : -1));
            }
            if (weeks > 0) {
                c.add(Calendar.WEEK_OF_YEAR, weeks * (future ? 1 : -1));
            }
            if (days > 0) {
                c.add(Calendar.DATE, days * (future ? 1 : -1));
            }
            if (hours > 0) {
                c.add(Calendar.HOUR_OF_DAY, hours * (future ? 1 : -1));
            }
            if (minutes > 0) {
                c.add(Calendar.MINUTE, minutes * (future ? 1 : -1));
            }
            if (seconds > 0) {
                c.add(Calendar.SECOND, seconds * (future ? 1 : -1));
            }
            Calendar max = new GregorianCalendar();
            max.add(Calendar.YEAR, 10);
            if (c.after(max)) {
                return max.getTimeInMillis();
            }
            return c.getTimeInMillis();
        } catch (Exception ignored) {

        }
        return 0L;
    }

    public static String formatDate(long time) {
        return TIME_FORMAT.format(new Date(time));
    }
}
