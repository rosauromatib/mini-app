package com.aat.application.views;

import com.aat.application.core.data.entity.ZJTEntity;
import com.aat.application.data.repository.BaseEntityRepository;
import com.aat.application.data.repository.StandardFormRepository;
import com.aat.application.data.service.BaseEntityService;
import com.aat.application.form.TimeLineCommonForm;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.Route;

@Route(value = "timeline", layout = MainLayout.class)
public class TimeLineFormView<T extends ZJTEntity> extends CommonView<T> {

    protected TimeLineCommonForm<T> form;
    protected final BaseEntityRepository<T> repository;

    public TimeLineFormView(StandardFormRepository<T> repository) {
        super(repository);
        this.repository = repository;
    }

    private void configureForm() {
        if (form != null)
            remove(form);
        form = new TimeLineCommonForm<>(entityClass, new BaseEntityService<>(repository));
        add(form);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        super.beforeEnter(event);
        if (entityClass != null)
            configureForm();
    }
}