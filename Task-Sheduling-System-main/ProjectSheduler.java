import java.util.*;

public class ProjectSheduler {

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
        boolean[] occupied = new boolean[MAX_WORKING_DAYS];

        missedProjects.clear();
        futureProjects.clear();

        for (int i = 0; i < busyTillDay; i++)
            occupied[i] = true;

        List<project> critical = new ArrayList<>();
        List<project> safe = new ArrayList<>();

        Map<project, Integer> remainingMap = new HashMap<>();

        // planning offset because work starts Monday
        int planningOffset =
                schedulingDay.equalsIgnoreCase("sunday") ? 1 : 2;

        // STEP 1: classify projects
        for (project p : projects) {

            int daysPassed =
                    getDaysBetween(
                            p.getSubmissionDay(),
                            schedulingDay);

            int remainingDeadline =
                    p.getDeadline() - (daysPassed + planningOffset);

            remainingMap.put(p, remainingDeadline);

            if (remainingDeadline < 0) {

                missedProjects.add(p);
            }
            else if (remainingDeadline <= MAX_WORKING_DAYS) {

                critical.add(p);
            }
            else {

                safe.add(p);
                futureProjects.add(p);
            }
        }

        // STEP 2: sort critical projects
        // by remaining deadline ASC
        // if same → revenue DESC
        critical.sort((a, b) -> {

            int remA = remainingMap.get(a);
            int remB = remainingMap.get(b);

            if (remA != remB)
                return Integer.compare(remA, remB);

            return Double.compare(
                    b.getExpectedRevenue(),
                    a.getExpectedRevenue());
        });

        // STEP 3: sort safe projects by revenue DESC
        safe.sort((a, b) ->
                Double.compare(
                        b.getExpectedRevenue(),
                        a.getExpectedRevenue()));

        // STEP 4: combine order
        List<project> ordered = new ArrayList<>();

        ordered.addAll(critical);
        ordered.addAll(safe);

        // STEP 5: schedule from Monday forward
        int dayPointer = busyTillDay;

        for (project p : ordered) {

            if (dayPointer >= MAX_WORKING_DAYS)
                break;

            schedule[dayPointer] = p;

            occupied[dayPointer] = true;

            futureProjects.remove(p);

            dayPointer++;
        }

        // STEP 6: collect result
        List<project> result = new ArrayList<>();

        for (int i = busyTillDay;
             i < MAX_WORKING_DAYS;
             i++) {

            if (schedule[i] != null)
                result.add(schedule[i]);
        }

        return result;
    }

    // calculate day difference
    public static int getDaysBetween(
            String submissionDay,
            String schedulingDay) {

        submissionDay =
                submissionDay.toLowerCase().trim();

        schedulingDay =
                schedulingDay.toLowerCase().trim();

        List<String> days = Arrays.asList(
                "monday",
                "tuesday",
                "wednesday",
                "thursday",
                "friday",
                "saturday",
                "sunday"
        );

        int subIndex = days.indexOf(submissionDay);
        int schIndex = days.indexOf(schedulingDay);

        if (subIndex == -1 || schIndex == -1)
            return 0;

        return schIndex - subIndex;
    }
}