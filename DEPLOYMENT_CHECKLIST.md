# 📋 Deployment Checklist

## Pre-deployment
- [ ] Code committed to GitHub
- [ ] Railway configuration files created
- [ ] Environment variables documented
- [ ] Redis configuration verified

## Railway Deployment
- [ ] Railway account created
- [ ] GitHub repository connected (optional)
- [ ] PostgreSQL database provisioned
- [ ] Redis service provisioned
- [ ] Auth service deployed (with Redis connection)
- [ ] Book service deployed
- [ ] Environment variables configured
- [ ] Health checks passing

## Frontend Deployment
- [ ] Netlify account created
- [ ] Frontend repository connected
- [ ] config.prod.js updated with backend URLs
- [ ] Build successful
- [ ] CORS configured properly

## Testing
- [ ] API endpoints responding
- [ ] Database connection working
- [ ] Frontend-backend communication working
- [ ] Authentication flow working
- [ ] Book listing/details working

## Post-deployment
- [ ] Domain configured (optional)
- [ ] SSL certificates working
- [ ] Performance monitoring set up
- [ ] Error logging configured

## Notes
- Backend URLs: 
  - Auth Service: https://your-auth-service.railway.app
  - Book Service: https://your-book-service.railway.app
- Frontend URL: https://your-frontend.netlify.app
- Database: PostgreSQL (managed by Railway)
- Cache: Redis (managed by Railway)
- Authentication uses Redis for session management

## Troubleshooting Commands
```bash
# Check Railway logs
railway logs

# Test API endpoints
curl https://your-auth-service.railway.app/actuator/health
curl https://your-book-service.railway.app/actuator/health

# Check CORS
curl -H "Origin: https://your-frontend.netlify.app"      -H "Access-Control-Request-Method: GET"      -H "Access-Control-Request-Headers: X-Requested-With"      -X OPTIONS      https://your-auth-service.railway.app/api/auth/health
```
