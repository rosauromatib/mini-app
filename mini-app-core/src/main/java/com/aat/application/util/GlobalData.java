package com.aat.application.util;

import com.aat.application.core.data.entity.ZJTEntity;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.vaadin.flow.component.Component;
import jakarta.persistence.*;
import jakarta.persistence.metamodel.IdentifiableType;
import jakarta.persistence.metamodel.Metamodel;
import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;


import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalData {
    public static Map<String, List<?>> listData = new HashMap<>();

    public static void addData(String headerName, Class<? extends ZJTEntity> entityClass) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("aat_persistence_unit");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            String strEntityClass = entityClass.getName();
            // Get the primary key name
            Metamodel metamodel = em.getMetamodel();
            IdentifiableType<?> identifiableType = metamodel.entity(metamodel.getClass().getClassLoader().loadClass(strEntityClass));
            String primaryKeyFieldName = identifiableType.getId(identifiableType.getIdType().getJavaType()).getName();

            // Create the query string
            String queryString = "SELECT p FROM " + entityClass.getSimpleName() + " p ORDER BY p." + primaryKeyFieldName;

            TypedQuery<?> query = em.createQuery(queryString, entityClass);
            List<?> results = query.getResultList();
            listData.put(headerName, results);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
            emf.close();
        }
    }

    public static List<?> getDataById(Class<?> entityClass, int id) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("aat_persistence_unit");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<?> results = null;
        try {
            String queryString = "SELECT p FROM " + entityClass.getSimpleName() + " p WHERE p.id = :id";
            TypedQuery<?> query = em.createQuery(queryString, entityClass);
            query.setParameter("id", id);
            results = query.getResultList();
        } catch (Exception e) {
            em.getTransaction().rollback();
        } finally {
            em.close();
            emf.close();
        }

        return results;
    }

    public static String getContentDisplayedInSelect(Object data) {
        String content = null;
        for (Field field : data.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().getSimpleName().equals("ContentDisplayedInSelect")) {
                    try {
                        content = (String) field.get(data);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return content;
    }

    public static <T> T convertToZJTEntity(Object entityData, Class<?> zjtEntityClass) {
        try {
            if (entityData instanceof HibernateProxy) {
                Hibernate.initialize(entityData);
                entityData = ((HibernateProxy) entityData).getHibernateLazyInitializer().getImplementation();
            }
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new Hibernate5Module());
            mapper.registerModule(new JavaTimeModule());
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String json = mapper.writeValueAsString(entityData);
            return (T) mapper.readValue(json, zjtEntityClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getPrimaryKeyField(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                return field;
            }
        }
        // Check superclass if no field found
        return getPrimaryKeyField(clazz.getSuperclass());
    }

    public static List<String> getFieldNamesWithAnnotation(Class<? extends Annotation> annotation, Class<?> entityClass) {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : entityClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(annotation)) {
                fieldNames.add(field.getName());
            }
        }
        return fieldNames;
    }

    public static void addData(String header) {
        String headerName = convertToStandard(header);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("aat_persistence_unit");
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        try {
            TypedQuery<ZJTEntity> query = em.createNamedQuery("findAll" + headerName, ZJTEntity.class);
            List<ZJTEntity> results = query.getResultList();
            listData.put(header, results);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
            emf.close();
        }
    }

    public static List<Component> findComponentsWithAttribute(Component parent, String attributeName, String attributeValue) {
        List<Component> matchingComponents = new ArrayList<>();

        parent.getChildren().forEach(child -> {
            if (attributeValue.equals(child.getElement().getAttribute(attributeName))) {
                matchingComponents.add(child);
            }
            matchingComponents.addAll(findComponentsWithAttribute(child, attributeName, attributeValue));
        });

        return matchingComponents;
    }

    public static List<Component> findComponentsWithAttribute(Component parent, String attributeName) {
        List<Component> matchingComponents = new ArrayList<>();

        parent.getChildren().forEach(child -> {
            if (child.getElement().hasAttribute(attributeName)) {
                matchingComponents.add(child);
            }

            matchingComponents.addAll(findComponentsWithAttribute(child, attributeName));
        });

        return matchingComponents;
    }

    public static String convertToStandard(String string) {
        if (string == null)
            return null;
        return string.substring(0, 1).toUpperCase()
                + string.substring(1);
    }
}