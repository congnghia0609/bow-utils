package be.bow.application;

import be.bow.application.annotations.EagerBowComponent;
import be.bow.util.SpringUtils;
import be.bow.util.Utils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.List;

@EagerBowComponent
public class ApplicationLifeCycle {

    private boolean applicationWasTerminated = false;

    @Autowired
    private ApplicationContext applicationContext;

    public ApplicationLifeCycle() {
    }

    public synchronized void terminateApplication() {
        if (!applicationWasTerminated) {
            List<? extends CloseableComponent> terminatableBeans = SpringUtils.getInstantiatedBeans(applicationContext, CloseableComponent.class);
            for (CloseableComponent object : terminatableBeans) {
                if (!(object instanceof LateCloseableComponent)) {
                    IOUtils.closeQuietly(object);
                }
            }
            for (CloseableComponent object : terminatableBeans) {
                if (object instanceof LateCloseableComponent) {
                    IOUtils.closeQuietly(object);
                }
            }
            applicationWasTerminated = true;
        }
    }


    public void waitUntilTerminated() {
        while (!applicationWasTerminated) {
            Utils.threadSleep(500);
        }
    }

}