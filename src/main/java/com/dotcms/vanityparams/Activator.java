package com.dotcms.vanityparams;

import org.osgi.framework.BundleContext;
import com.dotcms.filters.interceptor.FilterWebInterceptorProvider;
import com.dotcms.filters.interceptor.WebInterceptorDelegate;
import com.dotmarketing.filters.AutoLoginFilter;
import com.dotmarketing.filters.InterceptorFilter;
import com.dotmarketing.osgi.GenericBundleActivator;
import com.dotmarketing.util.Config;

public class Activator extends GenericBundleActivator {

    final VanityUrlInterceptor vanityUrlInterceptor = new VanityUrlInterceptor();


    public void start(BundleContext context) throws Exception {

        // Initializing services...        initializeServices(context);



        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                        FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(InterceptorFilter.class);


        delegate.addFirst(vanityUrlInterceptor);

    }

    public void stop(BundleContext context) throws Exception {


        final FilterWebInterceptorProvider filterWebInterceptorProvider =
                        FilterWebInterceptorProvider.getInstance(Config.CONTEXT);

        final WebInterceptorDelegate delegate = filterWebInterceptorProvider.getDelegate(InterceptorFilter.class);

        delegate.remove(vanityUrlInterceptor.getName(), true);

    }

}
