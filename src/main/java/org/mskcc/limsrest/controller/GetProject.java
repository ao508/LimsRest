package org.mskcc.limsrest.controller;

import java.util.concurrent.Future;
import java.util.List;
import java.util.LinkedList;

import org.mskcc.limsrest.ConnectionPoolLIMS;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.mskcc.limsrest.service.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@RestController
@RequestMapping("/")
public class GetProject {
    private static Log log = LogFactory.getLog(GetProject.class);
    private final ConnectionPoolLIMS conn;
   
    public GetProject(ConnectionPoolLIMS conn) {
        this.conn = conn;
    }

    @RequestMapping("/getPmProject")  // POST method called by REX
    public List<RequestSummary> getContent(@RequestParam(value = "project") String[] project, @RequestParam(value = "filter", defaultValue = "false") String filter) {
        List<RequestSummary> rss = new LinkedList<>();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < project.length; i++) {
            if (!Whitelists.requestMatches(project[i])) {
                log.info("FAILURE: project is not using a valid format");
                return rss;
            } else {
                sb.append(project[i]);
                if (i < project.length - 1) {
                    sb.append(",");
                }
            }
        }
        log.info("Starting get PM project for projects: " + sb.toString());

        GetSamples task = new GetSamples();
        task.init(project, filter.toLowerCase());
        Future<Object> result = conn.submitTask(task);
        try {
            rss = (List<RequestSummary>) result.get();
        } catch (Exception e) {
            RequestSummary rs = new RequestSummary();
            rs.setInvestigator(e.getMessage());
            rss.add(rs);
        }
        return rss;
    }
}