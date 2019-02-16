package com.goodforgoodbusiness.engine.route;

import static java.util.stream.Collectors.toSet;

import org.apache.log4j.Logger;

import com.goodforgoodbusiness.engine.dht.DHTSearcher;
import com.goodforgoodbusiness.engine.store.container.ContainerStore;
import com.goodforgoodbusiness.model.TriTuple;
import com.goodforgoodbusiness.shared.encode.JSON;
import com.goodforgoodbusiness.webapp.ContentType;
import com.goodforgoodbusiness.webapp.error.BadRequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import spark.Request;
import spark.Response;
import spark.Route;

@Singleton
public class MatchSearchRoute implements Route {
	private static final Logger log = Logger.getLogger(MatchSearchRoute.class);
	
	private final ContainerStore store;
	private final DHTSearcher searcher;
	
	@Inject
	public MatchSearchRoute(ContainerStore store, DHTSearcher searcher) {
		this.store = store;
		this.searcher = searcher;
	}
	
	@Override
	public Object handle(Request req, Response res) throws Exception {
		res.type(ContentType.json.getContentTypeString());
		
		var tuple = JSON.decode(req.queryParams("pattern"), TriTuple.class);
		if (tuple != null) {
			log.info("Matches called for " + tuple);
			if (!tuple.getSubject().isPresent() && !tuple.getObject().isPresent()) {
				throw new BadRequestException("Searching DHT for (?, _, ?) or (_, _ , _) not supported");
			}
			
			// search for remote containers, store in local store
			var newContainers = searcher.search(tuple, true).collect(toSet());
			if (log.isDebugEnabled()) log.debug("new = " + newContainers);
			
			// retrieve all containers from local store
			// includes those just fetched and others we already knew.
			var knownContainers = store.search(tuple).collect(toSet());
			if (log.isDebugEnabled()) log.debug("known = " + knownContainers);
			
			// return known containers
			return JSON.encode(knownContainers);
		}
		else {
			throw new BadRequestException("Must specify a triple");
		}
	}
}
