package com.liferay.i18n.language;

import com.liferay.portal.kernel.language.UTF8Control;
import org.osgi.service.component.annotations.Component;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * The Class GlobalLanguageEnUSResourceBundle to override and create new global english US languages.
 * @author Roselaine Marques
 */
@Component(
    immediate=true,
    property={
       "language.id=en_US"
    },
    service = ResourceBundle.class
)
public class GlobalLanguageEnUSResourceBundle extends ResourceBundle{

    /**
     *  Get the list of keys in the resource bundle
     *
     *  @return the key’s list
     */
    @Override
    protected Object handleGetObject(String key) {
        return _resourceBundle.getObject(key);
    }

    /**
     *  Looks up the key in the module’s resource bundle
     *  (which is based on the module’s language properties file)
     *
     *  @param key from lookup the value message
     *  @return the key’s value as an Object
     */
    @Override
    public Enumeration<String> getKeys() {
        return _resourceBundle.getKeys();
    }

    /** Instance the resource bundle object to load a Language file. */
    private final ResourceBundle _resourceBundle = ResourceBundle.getBundle("content.Language_en_AU", UTF8Control.INSTANCE);
}