package com.ge.predix.solsvc.fdh.handler.asset.helper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import com.ge.predix.entity.moduleconfigroot.ModuleConfigRoot;

/**
 * 
 * @author predix -
 */
public class PmpModuleConfigRootParser extends
        GenericJaxbParser<ModuleConfigRoot> implements IOsacbmXMLParsing {

    /**
     *  -
     */
    public PmpModuleConfigRootParser() {
        super(ModuleConfigRoot.class);
    }

    /**
     * @param classesToBeBound -
     */
    @SuppressWarnings("nls")
    public PmpModuleConfigRootParser(Class<?>[] classesToBeBound) {
        try {
            Class<?>[] classes = new Class[classesToBeBound.length + 1];
            System.arraycopy(classesToBeBound, 0, classes, 0, classesToBeBound.length);
            classes[classesToBeBound.length] = ModuleConfigRoot.class;
            setJaxbContext(JAXBContext.newInstance(classes));
        } catch (JAXBException e) {
            throw new RuntimeException("Unable to initialize JAXBContext", e);
        }
    }

}
