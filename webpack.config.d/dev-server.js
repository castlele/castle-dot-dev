// Reloading /feed/ must serve index.html, as Caddy does in production.
config.devServer = config.devServer || {};
config.devServer.historyApiFallback = true;
