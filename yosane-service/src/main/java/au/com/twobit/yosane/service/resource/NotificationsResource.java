package au.com.twobit.yosane.service.resource;

import static au.com.twobit.yosane.service.resource.ResourceHelper.createLink;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import au.com.twobit.yosane.service.resource.annotations.Relation;
import au.com.twobit.yosane.service.resource.dto.Notification;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.theoryinpractise.halbuilder.DefaultRepresentationFactory;
import com.theoryinpractise.halbuilder.api.Link;
import com.theoryinpractise.halbuilder.api.Representation;
import com.theoryinpractise.halbuilder.api.RepresentationFactory;

@Path("/yosane/notifications")
public class NotificationsResource {
    private static final int MAX_NOTIFICATIONS = 100;
    private static final String METHOD_GET_NOTIFICATIONS = "GET NOTIFICATIONS";
    private EventBus eventBus;
    private LinkedBlockingQueue<Notification> notifications = Queues.newLinkedBlockingQueue(MAX_NOTIFICATIONS);
    private DefaultRepresentationFactory hal;
    
    @Inject
    public NotificationsResource(EventBus eventBus, DefaultRepresentationFactory hal) {
        this.eventBus = eventBus;
        this.hal=hal;
        setupEventBus();        
    }
    
    void setupEventBus() {
        eventBus.register(this);
    }
    
    @Subscribe
    public void changeEvent(Notification notification) {
        notifications.offer( notification );
        System.out.println(notifications.peek());
    }
    
    @GET
    @Relation(relation="notification",method=METHOD_GET_NOTIFICATIONS)
    public Response getNotifications() {
        List<Notification> msgs = Lists.newArrayList();
        notifications.drainTo(msgs);
        Collections.reverse(msgs);
        String pathbase = getClass().getAnnotation(Path.class).value();
        Link home = createLink(HomeResource.class);
        Link scanners = createLink(ScannersResource.class);
        Representation response = hal.newRepresentation(pathbase)
                .withLink( home.getRel(), home.getHref())
                .withLink( scanners.getRel(), scanners.getHref());
        response.withProperty("notifications",msgs);
        return Response.ok(response.toString( RepresentationFactory.HAL_JSON)).build();
    }

}
