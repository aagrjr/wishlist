mongo --eval "db.auth('$MONGO_INITDB_ROOT_USERNAME', '$MONGO_INITDB_ROOT_PASSWORD'); db = db.getSiblingDB('wishlist'); db.createUser({ user: 'wishlist-user', pwd: 'wishlist-password', roles: [{ role: 'readWrite', db: 'wishlist' }, { role: 'dbAdmin', db: 'wishlist'}] });"