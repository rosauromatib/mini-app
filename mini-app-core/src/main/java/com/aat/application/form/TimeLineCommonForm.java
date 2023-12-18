package com.aat.application.form;

import com.aat.application.core.data.entity.ZJTEntity;
import com.aat.application.core.data.service.ZJTService;
import com.aat.application.core.form.TimeLineForm;
import com.aat.application.data.repository.TimelineRepository;
import com.aat.application.data.service.TimelineService;

public class TimeLineCommonForm<T extends ZJTEntity> extends TimeLineForm<T> {
	public TimeLineCommonForm(Class<T> entityClass, TimelineService service, String groupName) {
        super(entityClass, service, groupName);
        addClassName("demo-app-form");
	}
}