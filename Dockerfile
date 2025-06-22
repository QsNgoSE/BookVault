FROM nginx:alpine

# Copy static files to nginx html directory
COPY *.html /usr/share/nginx/html/
COPY asset/ /usr/share/nginx/html/asset/
COPY data/ /usr/share/nginx/html/data/
COPY vendor/ /usr/share/nginx/html/vendor/
COPY config.js /usr/share/nginx/html/

# Copy custom nginx configuration if needed
# COPY nginx.conf /etc/nginx/nginx.conf

# Expose port 80
EXPOSE 80

# Start nginx
CMD ["nginx", "-g", "daemon off;"] 