package cronjob;

import classes.Complaint;
import database.ComplaintDBController;
import database.UserDBController;
import emails.EmailManager;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import utils.Constants;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This Job is being called from [CronJobScheduler] each second
 * and send email to all workers that has not treated a complaint within 24 hours
 * @see CronjobScheduler
 */
public class MonitorComplaintsTreatmentJob implements Job {
    private static Set<Integer> complaintCurrentlyBeingTreated = new HashSet<>();
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        List<Complaint> complaintsThatNeedsToBeNotified = ComplaintDBController.getComplaintsThatNeedsToBeNotified();
        if (complaintsThatNeedsToBeNotified != null) {
            for (Complaint complaint : complaintsThatNeedsToBeNotified) {
                if (!complaintCurrentlyBeingTreated.contains(complaint.getId())) {
                    complaintCurrentlyBeingTreated.add(complaint.getId());
                    boolean isSuccess = EmailManager.sendEmail(
                            UserDBController.getEmailByUserId(complaint.getCustomerServiceWorkerId()),
                            "🌷 You need to treat complaint #" + complaint.getId() + " 🌷",
                            "Complaint #" + complaint.getId(),
                            "The complaint <b>was not getting treatment within 24 hours.</b><br>It was issued on the " + Constants.SQL_DATE_FORMAT.format(complaint.getDate()) + "<br>Please treat it ASAP!",
                            "mail_template.html"
                    );
                    if (isSuccess) {
                        ComplaintDBController.setNotificationWasSent(complaint.getId());
                    }
                }
            }
        }
    }
}
