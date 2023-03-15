package me.lucthesloth.slothgpt;

import java.util.List;

public class ChatRequestData {
    private String model;
    private List<Message> messages;
    private int max_tokens;
    private int n;

    public ChatRequestData(String model, List<Message> messages, int max_tokens, int n) {
        this.model = model;
        this.messages = messages;
        this.max_tokens = max_tokens;
        this.n = n;
    }

    // getters and setters
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public int getMax_tokens() {
        return max_tokens;
    }

    public void setMax_tokens(int max_tokens) {
        this.max_tokens = max_tokens;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public static class Message {
        private String role;
        private String content;

        public Message(String author, String content) {
            this.role = author;
            this.content = content;
        }

        // getters and setters
        public String getRole() {
            return role;
        }

        public void setRole(String author) {
            this.role = author;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static class Response {
        private String id;
        private String object;
        private int created;

        private List<Choices> choices;
        
        public Response(String id, String object, int created, List<Choices> choices) {
            this.id = id;
            this.object = object;
            this.created = created;
            this.choices = choices;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getObject() {
            return object;
        }
        public void setObject(String object) {
            this.object = object;
        }
        public int getCreated() {
            return created;
        }
        public void setCreated(int created) {
            this.created = created;
        }
        public List<Choices> getChoices() {
            return choices;
        }
        public void setChoices(List<Choices> choices) {
            this.choices = choices;
        }        

        public static class Usage {
            private int prompt_tokens;
            private int completion_tokens;
            private int total_tokens;

            public Usage(int prompt_tokens, int completion_tokens, int total_tokens) {
                this.prompt_tokens = prompt_tokens;
                this.completion_tokens = completion_tokens;
                this.total_tokens = total_tokens;
            }
            public int getPrompt_tokens() {
                return prompt_tokens;
            }
            public void setPrompt_tokens(int prompt_tokens) {
                this.prompt_tokens = prompt_tokens;
            }
            public int getCompletion_tokens() {
                return completion_tokens;
            }
            public void setCompletion_tokens(int completion_tokens) {
                this.completion_tokens = completion_tokens;
            }
            public int getTotal_tokens() {
                return total_tokens;
            }
            public void setTotal_tokens(int total_tokens) {
                this.total_tokens = total_tokens;
            }

        }
        public static class Choices {
            private int index;
            private Message message;
            private String finish_reason;       
            
            public Choices (int index, Message message, String finish_reason) {
                this.index = index;
                this.message = message;
                this.finish_reason = finish_reason;
            }
            public int getIndex() {
                return index;
            }
            public void setIndex(int index) {
                this.index = index;
            }
            public Message getMessage() {
                return message;
            }
            public void setMessage(Message message) {
                this.message = message;
            }
            public String getFinish_reason() {
                return finish_reason;
            }
            public void setFinish_reason(String finish_reason) {
                this.finish_reason = finish_reason;
            }

        }
    }
}
