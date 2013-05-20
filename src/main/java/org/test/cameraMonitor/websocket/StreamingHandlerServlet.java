/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.test.cameraMonitor.websocket;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Example web socket servlet for chat.
 */
public class StreamingHandlerServlet extends WebSocketServlet {


    private final AtomicInteger connectionIds = new AtomicInteger(0);
    private final Set<StreamHandlerMessageInbound> connections =
            new CopyOnWriteArraySet<StreamHandlerMessageInbound>();

    protected StreamInbound createWebSocketInbound(String subProtocol,
                                                   HttpServletRequest request) {
        String id = request.getParameter("streamId");
        return new StreamHandlerMessageInbound(id);
    }

    public final class StreamHandlerMessageInbound extends MessageInbound {

        StreamingHandlerEngine engine;

        public StreamHandlerMessageInbound(String streamId) {
            this.engine = new StreamingHandlerEngine(this, streamId);
        }

        @Override
        protected void onOpen(WsOutbound outbound) {
            connections.add(this);
        }

        @Override
        protected void onClose(int status) {

        }

        @Override
        protected void onBinaryMessage(ByteBuffer message) throws IOException {
            throw new UnsupportedOperationException(
                    "Binary message not supported.");
        }

        @Override
        protected void onTextMessage(CharBuffer message) throws IOException {
            engine.handleClientUpdate(message);
        }

        public void broadcast(String message) {
            for (StreamHandlerMessageInbound connection : connections) {
                try {
                    CharBuffer buffer = CharBuffer.wrap(message);
                    connection.getWsOutbound().writeTextMessage(buffer);
                } catch (IOException ignore) {
                    // Ignore
                }
            }
        }
    }
}