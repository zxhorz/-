package com.hengtiansoft.bluemorpho.workbench.quartz.job;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.hengtiansoft.bluemorpho.workbench.domain.User;
import com.hengtiansoft.bluemorpho.workbench.enums.UserState;
import com.hengtiansoft.bluemorpho.workbench.repository.UserRepository;

public class UserCheckJob implements Job {
    private static final Logger LOGGER = Logger.getLogger(UserCheckJob.class);
    @Autowired
    private UserRepository userRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            LOGGER.info("Start userCheck job.");
            Iterable<User> users = userRepository.findAll();
            List<User> list = new ArrayList<User>();
            users.forEach(single -> {
                list.add(single);
            });
            list.forEach(user -> {
                String time = user.getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    if (user.getState().equals(UserState.NON_ACTIVE.toString())) {
                        Date date = sdf.parse(time);
                        Date currentDate = new Date();
                        long diff = currentDate.getTime() - date.getTime();
                        float hour = diff / (60 * 60 * 1000);
                        if (hour > 24.0) {
                            userRepository.delete(user);
                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    LOGGER.error(e);
                }

            });

        } catch (Exception e) {
            LOGGER.error("Failed to open http connecton", e);
        }
        LOGGER.info("End userCheck job.");
    }
}
