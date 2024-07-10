/**
 * Copyright (c) 2012 to original author or authors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.sonatype.maven.polyglot.groovy.builder;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.Singleton;
import groovy.util.Factory;
import groovy.util.FactoryBuilderSupport;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.maven.model.Activation;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.Extension;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.Profile;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.maven.polyglot.execute.ExecuteManager;
import org.sonatype.maven.polyglot.execute.ExecuteTask;
import org.sonatype.maven.polyglot.groovy.builder.factory.ChildFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.DependencyFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ExcludesFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ExclusionFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ExclusionsFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ExecuteFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ExecutionFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ExtensionsFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.GoalsFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.IncludesFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ListFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ModelFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ModulesFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.NamedFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ObjectFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ParentFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.PluginFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.PropertiesFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ReportSetsFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ReportingFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.ReportsFactory;
import org.sonatype.maven.polyglot.groovy.builder.factory.StringFactory;

/**
 * Builds Maven {@link Model} instances.
 *
 * @author <a href="mailto:jason@planet57.com">Jason Dillon</a>
 *
 * @since 0.7
 */
@Singleton
@Named
public class ModelBuilder extends FactoryBuilderSupport {
    protected Logger log = LoggerFactory.getLogger(ModelBuilder.class);

    private final Set<String> factoryNames = new HashSet<String>();

    private final Set<Class> factoryTypes = new HashSet<Class>();

    private final List<ExecuteTask> tasks = new ArrayList<ExecuteTask>();

    @Inject
    private ExecuteManager executeManager;

    public ModelBuilder() {
        registerFactories();
    }

    /**
     * Delegate to force use of invokeMethod when building the model.
     */
    private final GroovyObject invokeDelegate = new GroovyObjectSupport() {
        @Override
        public Object invokeMethod(final String name, final Object args) {
            return ModelBuilder.this.invokeMethod(name, args);
        }
    };

    @Override
    protected void setClosureDelegate(final Closure c, final Object o) {
        c.setDelegate(invokeDelegate);
        c.setResolveStrategy(Closure.DELEGATE_FIRST);
    }

    public ExecuteManager getExecuteManager() {
        return executeManager;
    }

    public List<ExecuteTask> getTasks() {
        return tasks;
    }

    public void registerFactories() {
        registerStringFactory("module");
        registerStringFactory("filter");
        registerStringFactory("include");
        registerStringFactory("exclude");
        registerStringFactory("goal");
        registerStringFactory("role");
        registerStringFactory("otherArchive");
        registerStringFactory("activeByDefault");
        registerStringFactory("report");

        registerFactory(new PluginFactory());
        registerFactoriesFor(Plugin.class);

        registerFactory(new ModulesFactory());
        registerFactory(new ExclusionsFactory());
        registerFactory(new ExtensionsFactory());
        registerFactory(new IncludesFactory());
        registerFactory(new ExcludesFactory());
        registerFactory(new GoalsFactory());
        registerFactory(new ExecuteFactory());
        registerFactory(new ReportSetsFactory());
        registerFactory(new ReportsFactory());

        registerFactory(new ReportingFactory());
        registerFactoriesFor(Reporting.class);

        registerFactory(new ExecutionFactory());
        registerFactoriesFor(PluginExecution.class);

        registerFactory(new ModelFactory());
        registerFactoriesFor(Model.class);

        registerChildFactory("dependency", Dependency.class);
        registerChildFactory("exclusion", Exclusion.class);
        registerChildFactory("extension", Extension.class);
        registerChildFactory("resource", Resource.class);
        registerChildFactory("testResource", Resource.class);
        registerChildFactory("notifier", Notifier.class);
        registerChildFactory("contributor", Contributor.class);
        registerChildFactory("developer", Developer.class);
        registerChildFactory("license", License.class);
        registerChildFactory("mailingList", MailingList.class);
        registerChildFactory("profile", Profile.class);
        registerChildFactory("repository", DeploymentRepository.class);
        registerChildFactory("pluginRepository", Repository.class);
        registerChildFactory("reportSet", ReportSet.class);
        registerChildFactory("activation", Activation.class);
    }

    @Override
    public void registerBeanFactory(final String name, final Class type) {
        super.registerBeanFactory(name, type);
        registerFactoriesFor(type);
    }

    @Override
    public void registerFactory(final String name, final String groupName, final Factory factory) {
        if (log.isDebugEnabled()) {
            log.debug("Registering factory: " + name + ", factory: " + factory);
            if (factoryNames.contains(name)) {
                log.warn("Duplicate factory: " + name);
            }
        }
        factoryNames.add(name);
        super.registerFactory(name, groupName, factory);
    }

    private void registerFactory(final NamedFactory factory) {
        assert factory != null;
        registerFactory(factory.getName(), null, factory);
    }

    private void registerChildFactory(final String name, final Class type) {
        registerFactory(createChildFactory(name, type));
        registerFactoriesFor(type);
    }

    private NamedFactory createChildFactory(final String name, final Class type) {
        assert name != null;
        assert type != null;

        if (type == Parent.class) {
            return new ParentFactory();
        }
        if (type == Dependency.class) {
            return new DependencyFactory();
        }
        if (type == Exclusion.class) {
            return new ExclusionFactory();
        }

        return new ChildFactory(name, type);
    }

    private void registerStringFactory(final String name) {
        registerFactory(new StringFactory(name));
    }

    private void registerListFactory(final String name) {
        registerFactory(new ListFactory(name));
    }

    private void registerPropertiesFactory(final String name) {
        registerFactory(new PropertiesFactory(name));
    }

    private void registerObjectFactory(final String name) {
        registerFactory(new ObjectFactory(name));
    }

    private void registerFactoriesFor(final Class type) {
        assert type != null;

        if (factoryTypes.contains(type)) {
            return;
        }
        factoryTypes.add(type);

        if (log.isDebugEnabled()) {
            log.debug("Registering factories for type: " + type);
        }

        Method[] methods = type.getMethods();
        for (Method method : methods) {
            if (isSetter(method)) {
                String name = propertyNameOf(method);

                if (factoryNames.contains(name)) {
                    continue;
                }

                Class param = method.getParameterTypes()[0];
                if (param == String.class) {
                    registerStringFactory(name);
                } else if (param == List.class) {
                    registerListFactory(name);
                } else if (param == Properties.class) {
                    registerPropertiesFactory(name);
                } else if (param == Object.class) {
                    registerObjectFactory(name);
                } else if (param.getName().startsWith("org.apache.maven.model.")) {
                    registerChildFactory(name, param);
                } else {
                    // Skip setters with unsupported types (model will use string versions)
                    if (log.isDebugEnabled()) {
                        log.debug("Skipping setter with unsupported type: " + method);
                    }
                }
            }
        }
    }

    private boolean isSetter(final Method method) {
        assert method != null;
        if (!method.getName().startsWith("set")) {
            return false;
        }

        if (method.getParameterTypes().length > 1) {
            return false;
        }
        if (method.getReturnType() != Void.TYPE) {
            return false;
        }

        int m = method.getModifiers();
        if (!Modifier.isPublic(m) || Modifier.isStatic(m)) {
            return false;
        }

        return true;
    }

    private String propertyNameOf(final Method method) {
        assert method != null;

        String name = method.getName();
        name = name.substring(3, name.length());

        return new StringBuffer(name.length())
                .append(Character.toLowerCase(name.charAt(0)))
                .append(name.substring(1))
                .toString();
    }

    public Object findInContext(final String key) {
        for (Map<String, Object> ctx : getContexts()) {
            if (ctx.containsKey(key)) {
                return ctx.get(key);
            }
        }

        return null;
    }

    // BEGIN: Kludgy Workaround
    // Explanation: When I translated the slf4j pom.xml to pom.groovy, I ended up with this:
    //  plugin {
    //    artifactId 'maven-project-info-reports-plugin'
    //    reportSets {
    //      reportSet
    //    }
    //  }
    // The ModelBuilder saw this, and assumed that it was trying to populate the reportSet method.  Note
    // that this shows up because the slf4j pom.xml includes
    // a <reportSets><reportSet><report/></reportSet></reportSets>
    private ReportSet reportSet;

    public ReportSet getReportSet() {
        return reportSet;
    }

    public void setReportSet(ReportSet reportSet) {
        this.reportSet = reportSet;
    }
    // END: Kludgy Workaround

}
