import React, {useState} from 'react';
import Sidebar from "react-sidebar";
import {BrowserRouter, Link, Route, Switch, useLocation} from "react-router-dom";
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';

import {AppRoute, routes} from "./routes";
import './App.scss';

const SidebarComponent: React.FC<{ title: string, routes: AppRoute[] }> = ({title, routes}) => {
    const location = useLocation();
    return (
        <div>
            <div className='app--title'>
                <p className='title'>{title}</p>
            </div>
            <List>
                {
                    routes.map((route, index) => (
                        <Link to={route.path} style={{ color: 'inherit', textDecoration: 'none' }} key={index}>
                            <ListItem button key={index} selected={location.pathname === route.path}>
                                <ListItemIcon>{route.icon}</ListItemIcon>
                                <ListItemText primary={route.label}/>
                            </ListItem>
                        </Link>
                    ))
                }
            </List>
        </div>
    )
};

const RoutesComponent: React.FC<{ routes: AppRoute[] }> = ({routes}) => (
    <React.Fragment>
        {
            routes.map((route, index) => (
                <div className='route--container'>
                    <Route key={index}
                           path={route.path}
                           exact={route.exact || false}
                           children={<route.component/>}/>
                </div>
            ))
        }
    </React.Fragment>
);

function App() {
    const [appRoutes,] = useState(routes);

    return (
        <BrowserRouter>
            <div id="main--app">
                <Sidebar
                    sidebar={<SidebarComponent title='AWS App' routes={appRoutes}/>}
                    open={true}
                    docked={true}
                >
                    <div className="main--content">
                        <Switch>
                            <RoutesComponent routes={appRoutes}/>
                        </Switch>
                    </div>
                </Sidebar>
            </div>
        </BrowserRouter>
    );
}

export default App;
