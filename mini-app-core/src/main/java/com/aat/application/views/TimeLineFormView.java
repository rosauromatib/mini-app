package com.aat.application.views;

import com.aat.application.core.data.entity.ZJTEntity;
import com.aat.application.data.repository.TimelineRepository;
import com.aat.application.data.service.TimelineService;
import com.aat.application.form.TimeLineCommonForm;
import com.vaadin.flow.dom.Element;
import com.vaadin.flow.router.BeforeEnterEvent;

//@Route(value = "timelineview")
public class TimeLineFormView<T extends ZJTEntity> extends CommonView<T>{

    protected TimeLineCommonForm<T> form;
    private final TimelineRepository repository;

    public TimeLineFormView(TimelineRepository repository) {
        super(null);
        this.repository = repository;
    }

    private void configureForm() {
        form = new TimeLineCommonForm<>(entityClass, new TimelineService(this.repository), groupName);
        setForm(form);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);

        if (entityClass != null) {
            configureForm();
        }
    }
}