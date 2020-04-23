package com.dotcms.vanityparams;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.dotcms.filters.interceptor.Result;
import com.dotcms.filters.interceptor.WebInterceptor;
import com.dotcms.vanityurl.handler.VanityUrlHandler;
import com.dotcms.vanityurl.handler.VanityUrlHandlerResolver;
import com.dotcms.vanityurl.model.VanityUrlResult;
import com.dotmarketing.beans.Host;
import com.dotmarketing.business.web.HostWebAPI;
import com.dotmarketing.business.web.LanguageWebAPI;
import com.dotmarketing.business.web.UserWebAPI;
import com.dotmarketing.business.web.WebAPILocator;
import com.dotmarketing.filters.CMSUrlUtil;

/**
 * This web interceptor adds the access control allow origin in addition to overrides the request and response
 * @author jsanca
 */
public class VanityUrlInterceptor implements WebInterceptor {

    private static final long serialVersionUID = 1L;
    private final VanityUrlHandlerResolver vanityUrlHandlerResolver;
    private final CMSUrlUtil urlUtil;
    private final HostWebAPI hostWebAPI;
    private final LanguageWebAPI languageWebAPI;
    private final UserWebAPI  userWebAPI;
    
    
    public VanityUrlInterceptor() {

        this (VanityUrlHandlerResolver.getInstance(), CMSUrlUtil.getInstance(),
                WebAPILocator.getHostWebAPI(), WebAPILocator.getLanguageWebAPI(),
                WebAPILocator.getUserWebAPI());
    }


    protected VanityUrlInterceptor(final VanityUrlHandlerResolver vanityUrlHandlerResolver,
                           final CMSUrlUtil urlUtil,
                           final HostWebAPI hostWebAPI,
                           final LanguageWebAPI languageWebAPI,
                           final UserWebAPI  userWebAPI) {

        this.vanityUrlHandlerResolver = vanityUrlHandlerResolver;
        this.urlUtil                  = urlUtil;
        this.hostWebAPI               = hostWebAPI;
        this.languageWebAPI           = languageWebAPI;
        this.userWebAPI               = userWebAPI;
    }
    
    
    @Override
    public String[] getFilters() {
        return new String[] {"/*"};
    }

    @Override
    public Result intercept(final HttpServletRequest request,
                            final HttpServletResponse response) throws IOException {

        Result result = Result.NEXT;


        //Get the URI from the request and check for possible XSS hacks
        final String uri         = this.urlUtil.getURIFromRequest(request);
        final boolean isFiltered = this.urlUtil.isVanityUrlFiltered (uri);
        //Getting the site form the request
        final Host site          = this.hostWebAPI.getCurrentHostNoThrow(request);

        if (!isFiltered) {

            //Get the user language
            final long languageId = this.languageWebAPI.getLanguage(request).getId();

            //Verify if the given URI is a VanityURL
            if (this.urlUtil.isVanityUrl(uri, site, languageId)) {

                //Find the Vanity URL handler and handle this given URI
                final VanityUrlHandler vanityUrlHandler = this.vanityUrlHandlerResolver.getVanityUrlHandler();
                final VanityUrlResult vanityUrlResult = vanityUrlHandler
                        .handle(uri, response, site, languageId, this.userWebAPI.getUser(request));

                //If the handler already resolved the requested URI we stop the processing here
                if (vanityUrlResult.isResolved()) {
                    return result;
                }
  

         
                result = new Result.Builder()
                                .next()
                                .wrap(new VanityUrlRequestWrapper(request, vanityUrlResult))
                               .build();
             
           }

        }


        return result;
    }

}
