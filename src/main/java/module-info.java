module org.example.apprestaurant {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires kotlin.stdlib;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires jakarta.persistence;
    requires static lombok;
    requires spring.context;
    requires spring.tx;
    requires spring.data.jpa;
    requires spring.data.commons;
    requires spring.beans;
    requires org.hibernate.orm.core;

    opens org.example.apprestaurant to javafx.fxml, spring.core, spring.beans, spring.context;
    opens org.example.apprestaurant.controller to javafx.fxml, spring.core, spring.beans, spring.context;
    opens org.example.apprestaurant.entity to spring.core, spring.beans, spring.context;
    opens org.example.apprestaurant.service to spring.core, spring.beans, spring.context;
    exports org.example.apprestaurant;
}