import java.util.List;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);

    static ProjectDoa dao = new ProjectDoa();

    static ProjectScheduler scheduler = new ProjectScheduler();

    public static void main(String[] args) throws Exception {

        while (true) {

            System.out.println("\n===== Intelligent Project Scheduler =====");

            System.out.println("1. Add Project");
            System.out.println("2. View Projects");
            System.out.println("3. Generate Schedule");
            System.out.println("4. Exit");

            System.out.print("Enter choice: ");

            int choice = scanner.nextInt();

            switch (choice) {

                case 1:
                    addProject();
                    break;

                case 2:
                    viewProjects();
                    break;

                case 3:
                    generateSchedule();
                    break;

                case 4:
                    System.exit(0);
            }
        }
    }

    // ADD PROJECT
    static void addProject() throws Exception {

        scanner.nextLine();

        System.out.print("Enter Title: ");
        String title = scanner.nextLine();

        System.out.print("Enter Submission Day (Mon-Sun): ");
        String submissionDay = scanner.nextLine();

        System.out.print("Enter Deadline (days): ");
        int deadline = scanner.nextInt();

        System.out.print("Enter Expected Revenue: ");
        double revenue = scanner.nextDouble();

        project p =
                new project(
                        title,
                        deadline,
                        revenue,
                        submissionDay);

        dao.addProject(p);

        System.out.println("Project added successfully!");
    }

    // VIEW PROJECTS
    static void viewProjects() throws Exception {

        List<project> list = dao.getAllProjects();

        System.out.println("\n===== Project List =====");

        if (list.isEmpty()) {

            System.out.println("No projects found.");
            return;
        }

        for (project p : list) {

            System.out.println(
                    p.getProjectId()
                            + " | "
                            + p.getTitle()
                            + " | Submission: "
                            + p.getSubmissionDay()
                            + " | Deadline: "
                            + p.getDeadline()
                            + " days | Revenue: "
                            + p.getExpectedRevenue());
        }
    }

    // GENERATE SCHEDULE
    static void generateSchedule() throws Exception {

        List<project> projects = dao.getAllProjects();

        if (projects.isEmpty()) {

            System.out.println("No projects available.");
            return;
        }

        scanner.nextLine();

        System.out.print("Enter Scheduling Day (Sat/Sun): ");
        String schedulingDay = scanner.nextLine().toLowerCase();

        System.out.print("Enter busy till day (0-5): ");
        int busyTill = scanner.nextInt();

        System.out.print("Enter last week avg: ");
        double lastWeek = scanner.nextDouble();

        System.out.print("Enter last three week avg: ");
        double lastThree = scanner.nextDouble();

        List<project> schedule =
                scheduler.generateOptimalSchedule(
                        projects,
                        schedulingDay,
                        busyTill,
                        lastWeek,
                        lastThree);

        System.out.println("\n===== Optimal Schedule for Next Week =====");

        int day = busyTill + 1;
        double total = 0;

        for (project p : schedule) {

            System.out.println(
                    "Day "
                            + day++
                            + " → "
                            + p.getTitle()
                            + " | Revenue: "
                            + p.getExpectedRevenue());

            total += p.getExpectedRevenue();
        }

        System.out.println("\nTotal Expected Profit: " + total);

        // MISSED PROJECTS
        System.out.println("\n===== Missed Projects =====");

        List<project> missed = scheduler.getMissedProjects();

        if (missed.isEmpty())
            System.out.println("None");
        else
            for (project p : missed)
                System.out.println(
                        p.getTitle()
                                + " | Deadline expired");

        // FUTURE PROJECTS (FULLY FIXED)
        System.out.println("\n===== Future Projects =====");

        List<project> future = scheduler.getFutureProjects();

        if (future.isEmpty()) {

            System.out.println("None");

        } else {

            for (project p : future) {

                // days from submission → scheduling day
                int daysPassed =
                        ProjectScheduler.getDaysBetween(
                                p.getSubmissionDay(),
                                schedulingDay);

                // days from scheduling day → next Monday
                int daysToNextMonday =
                        ProjectScheduler.getDaysBetween(
                                schedulingDay,
                                "monday");

                // total days passed until next Monday
                int totalDays =
                        daysPassed + daysToNextMonday;

                // remaining on next Monday
                int remainingNextMonday =
                        p.getDeadline() - totalDays;

                // subtract only 5 working days
                int remainingAfterNextWeek =
                        remainingNextMonday - 5;

                if (remainingAfterNextWeek < 0)
                    remainingAfterNextWeek = 0;

                System.out.println(
                        p.getTitle()
                                + " | Original Deadline: "
                                + p.getDeadline()
                                + " days"
                                + " | Remaining Deadline After Next Week: "
                                + remainingAfterNextWeek
                                + " days");
            }
        }
    }
}