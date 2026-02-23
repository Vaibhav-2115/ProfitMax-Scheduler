import java.util.*;

public class ProjectScheduler {

    private static final int MAX_WORKING_DAYS = 5;

    private List<project> missedProjects = new ArrayList<>();
    private List<project> futureProjects = new ArrayList<>();

    public List<project> getMissedProjects() {
        return missedProjects;
    }

    public List<project> getFutureProjects() {
        return futureProjects;
    }

    public List<project> generateOptimalSchedule(
            List<project> projects,
            String schedulingDay,
            int busyTillDay,
            double lastWeekAvg,
            double lastThreeWeekAvg) {

        project[] schedule = new project[MAX_WORKING_DAYS];

        missedProjects.clear();
        futureProjects.clear();

        List<project> critical = new ArrayList<>();
        List<project> safe = new ArrayList<>();

        Map<project, Integer> remainingMap = new HashMap<>();

        int planningOffset =
                schedulingDay.equalsIgnoreCase("sunday") ? 1 : 2;

        for (project p : projects) {

            int currentRemaining =
                    p.getRemainingDeadline();

            int daysPassed =
                    getDaysBetween(
                            p.getSubmissionDay(),
                            schedulingDay);

            // First run calculation
            if (currentRemaining == p.getDeadline()) {

                currentRemaining =
                        p.getDeadline()
                                - (daysPassed + planningOffset);

            } else {

                // Next week → reduce 7 days
                currentRemaining =
                        currentRemaining - 7;
            }

            p.setRemainingDeadline(currentRemaining);

            remainingMap.put(p, currentRemaining);

            if (currentRemaining < 0)
                missedProjects.add(p);

            else if (currentRemaining <= MAX_WORKING_DAYS)
                critical.add(p);

            else {
                safe.add(p);
                futureProjects.add(p);
            }
        }

        // sort critical
        critical.sort((a, b) -> {

            int remA = remainingMap.get(a);
            int remB = remainingMap.get(b);

            if (remA != remB)
                return Integer.compare(remA, remB);

            return Double.compare(
                    b.getExpectedRevenue(),
                    a.getExpectedRevenue());
        });

        // sort safe
        safe.sort((a, b) ->
                Double.compare(
                        b.getExpectedRevenue(),
                        a.getExpectedRevenue()));

        List<project> ordered =
                new ArrayList<>();

        ordered.addAll(critical);
        ordered.addAll(safe);

        int dayPointer =
                busyTillDay;

        for (project p : ordered) {

            if (dayPointer >= MAX_WORKING_DAYS)
                break;

            schedule[dayPointer] = p;

            futureProjects.remove(p);

            dayPointer++;
        }

        List<project> result =
                new ArrayList<>();

        for (int i = busyTillDay;
             i < MAX_WORKING_DAYS;
             i++)

            if (schedule[i] != null)
                result.add(schedule[i]);

        return result;
    }

    public static int getDaysBetween(
            String submissionDay,
            String schedulingDay) {

        List<String> days =
                Arrays.asList(
                        "monday",
                        "tuesday",
                        "wednesday",
                        "thursday",
                        "friday",
                        "saturday",
                        "sunday");

        int sub =
                days.indexOf(
                        submissionDay.toLowerCase());

        int sch =
                days.indexOf(
                        schedulingDay.toLowerCase());

        int diff =
                sch - sub;

        if (diff < 0)
            diff += 7;

        return diff;
    }
}