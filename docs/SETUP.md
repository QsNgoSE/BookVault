# BookVault Frontend Setup Guide

This guide will help you set up and run the BookVault frontend application.

## Prerequisites

Before you begin, ensure you have the following installed:

- **Web Browser**: Modern browser (Chrome, Firefox, Safari, Edge)
- **Web Server**: Any HTTP server for local development (optional)
- **Text Editor**: VS Code, Sublime Text, or any preferred editor

## Project Structure

```
BookVault/
├── index.html              # Landing page
├── booklisting.html        # Book catalog
├── book-details.html       # Individual book details
├── about.html              # About us page
├── contact.html            # Contact page with map
├── help.html               # Help center
├── login.html              # User login
├── register.html           # User registration
├── user.html               # User dashboard
├── seller.html             # Seller dashboard
├── admin.html              # Admin dashboard
├── config.js               # Frontend configuration
├── .env.example            # Environment variables template
│
├── asset/
│   ├── css/
│   │   ├── bookvault.css   # Main stylesheet
│   │   ├── components.css  # Reusable components
│   │   └── [page].css      # Page-specific styles
│   ├── js/
│   │   └── main.js         # Main JavaScript file
│   ├── img/
│   │   ├── logo/           # Logo files
│   │   ├── books/          # Book cover images
│   │   └── team/           # Team member photos
│   └── fonts/              # Custom fonts
│
├── data/
│   └── books.json          # Demo book data
├── vendor/                 # Third-party libraries (optional)
└── docs/                   # Documentation
```

## Quick Start

### 1. Download the Project

```bash
# Clone or download the project
git clone <repository-url>
cd BookVault
```

### 2. Environment Setup

```bash
# Copy environment template
cp .env.example .env

# Edit the .env file with your settings
# Update API endpoints, API keys, etc.
```

### 3. Open in Browser

#### Option A: Direct File Access
Simply open `index.html` in your web browser.

#### Option B: Local HTTP Server (Recommended)

**Using Python:**
```bash
# Python 3
python3 -m http.server 8000

# Python 2
python -m SimpleHTTPServer 8000
```

**Using Node.js (npx):**
```bash
npx http-server . -p 8000
```

**Using PHP:**
```bash
php -S localhost:8000
```

Then open `http://localhost:8000` in your browser.

### 4. Configure API Connection

Update the API base URL in `config.js`:

```javascript
API: {
    BASE_URL: 'http://localhost:8080/api', // Your backend URL
    // ... other settings
}
```

## Configuration

### Environment Variables

The application uses environment variables for configuration. Create a `.env` file:

```env
# API Configuration
API_BASE_URL=http://localhost:8080/api

# Google Maps API (for contact page)
GOOGLE_MAPS_API_KEY=your_api_key_here

# Analytics
GOOGLE_ANALYTICS_ID=your_ga_id_here
```

### Frontend Configuration

Main configuration is in `config.js`. Key settings include:

- **API Endpoints**: Backend service URLs
- **Feature Flags**: Enable/disable features
- **UI Settings**: Colors, animations, breakpoints
- **Validation Rules**: Form validation patterns

## Features

### Current Features ✅

- **Responsive Design**: Mobile-first approach
- **Book Catalog**: Browse and search books
- **User Authentication**: Login/register (UI only)
- **User Dashboard**: Profile, orders, wishlist
- **Seller Dashboard**: Manage books and sales
- **Admin Dashboard**: Platform management
- **Contact Page**: Contact form and map
- **Help Center**: FAQ and support

### Upcoming Features 🚧

- **Shopping Cart**: Add to cart functionality
- **Payment Integration**: Stripe/PayPal
- **Real-time Notifications**: WebSocket integration
- **Social Login**: OAuth2 integration
- **Progressive Web App**: PWA features

## Page-by-Page Guide

### Landing Page (`index.html`)
- Hero section with search
- Featured books
- Category navigation
- Newsletter signup

### Book Listing (`booklisting.html`)
- Grid/list view
- Filtering and sorting
- Search functionality
- Pagination

### Book Details (`book-details.html`)
- Book information
- Reviews and ratings
- Related books
- Add to cart/wishlist

### User Dashboard (`user.html`)
- Profile management
- Order history
- Wishlist
- Recommendations

### Seller Dashboard (`seller.html`)
- Book management
- Sales analytics
- Order management
- Profile settings

### Admin Dashboard (`admin.html`)
- User management
- Platform analytics
- Content moderation
- System settings

## Customization

### Styling

1. **Colors**: Update CSS variables in `bookvault.css`
2. **Fonts**: Add custom fonts to `asset/fonts/`
3. **Components**: Modify `components.css` for UI elements
4. **Pages**: Edit individual page CSS files

### Content

1. **Logo**: Replace files in `asset/img/logo/`
2. **Images**: Add images to respective folders
3. **Data**: Update `data/books.json` for demo content
4. **Text**: Edit HTML files directly

### JavaScript

1. **Configuration**: Update `config.js`
2. **Functionality**: Modify `asset/js/main.js`
3. **New Features**: Add additional JS files

## Integration with Backend

The frontend is designed to work with a Java/Spring Boot backend:

### API Integration

1. **Authentication**: JWT token-based
2. **REST APIs**: Standard HTTP methods
3. **Error Handling**: Centralized error management
4. **Data Validation**: Client and server-side

### Required Backend Endpoints

```
POST /api/auth/login          # User login
POST /api/auth/register       # User registration
GET  /api/books              # List books
GET  /api/books/{id}         # Book details
GET  /api/users/profile      # User profile
... (see config.js for full list)
```

## Deployment

### Static Hosting

The frontend can be deployed to any static hosting service:

- **Netlify**: Drag and drop deployment
- **Vercel**: Git-based deployment
- **GitHub Pages**: Free hosting for public repos
- **AWS S3**: Scalable static hosting
- **CDN**: Use CloudFront, CloudFlare, etc.

### Build Process (Optional)

For production optimization:

```bash
# Minify CSS
npx clean-css-cli -o asset/css/bookvault.min.css asset/css/*.css

# Minify JavaScript
npx uglify-js asset/js/main.js -o asset/js/main.min.js

# Optimize Images
npx imagemin asset/img/**/* --out-dir=dist/asset/img/
```

## Troubleshooting

### Common Issues

1. **CORS Errors**
   - Enable CORS on backend
   - Use proper HTTP server for local development

2. **API Connection Failed**
   - Check backend server is running
   - Verify API URLs in config.js
   - Check network connectivity

3. **Images Not Loading**
   - Verify image paths
   - Check file permissions
   - Ensure images exist

4. **JavaScript Errors**
   - Check browser console
   - Verify all scripts are loaded
   - Check for syntax errors

### Browser Compatibility

- **Chrome**: 70+
- **Firefox**: 65+
- **Safari**: 12+
- **Edge**: 80+
- **Mobile**: iOS 12+, Android 8+

## Testing

### Manual Testing

1. **Functionality**: Test all user flows
2. **Responsiveness**: Test on different devices
3. **Cross-browser**: Test on multiple browsers
4. **Performance**: Check loading times

### Automated Testing (Future)

- **Unit Tests**: Jest framework
- **E2E Tests**: Cypress or Selenium
- **Performance**: Lighthouse CI
- **Accessibility**: axe-core

## Support

For help and support:

- **Documentation**: Check the docs/ folder
- **Issues**: Report bugs on the project repository
- **Email**: Contact the development team
- **Community**: Join our Discord/Slack

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License. See LICENSE file for details.

---

**Happy coding! 🚀** 