package com.goodforgoodbusiness.engine.webapp.rpc;

import java.util.Set;
import java.util.concurrent.ExecutorService;

import com.goodforgoodbusiness.rpclib.server.RPCHandler;
import com.goodforgoodbusiness.rpclib.server.receiver.RPCReceiver;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Handles RPC requests sent in as protocol buffers and dispatches them to the 
 * logic that sits behind each.
 */
@Singleton
public class DHTRPCHandler extends RPCHandler {
	@Inject
	public DHTRPCHandler(ExecutorService executorService, Set<RPCReceiver> rpcs) {
		super(executorService, rpcs);
	}
}
