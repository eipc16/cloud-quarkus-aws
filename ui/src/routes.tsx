import React from 'react';
import { SvgIconProps } from '@material-ui/core';
import WorkIcon from '@material-ui/icons/Work';
import ImageIcon from '@material-ui/icons/Image';
import PhotoLibraryIcon from '@material-ui/icons/PhotoLibrary';
import {BucketsComponent} from "./components/buckets/BucketsComponent";
import {ImagesComponent} from "./components/images/ImagesComponent";

export var routes: AppRoute[] = [
    {
        path: "/",
        exact: true,
        label: 'Buckets',
        icon: <WorkIcon />,
        component: () => <BucketsComponent />
    },
    {
        path: "/images",
        label: 'Images',
        icon: <PhotoLibraryIcon />,
        component: () => <ImagesComponent />
    },
    {
        path: "/rekognition",
        label: 'Rekognition',
        icon: <ImageIcon />,
        component: () => <div>Rekognition</div>
    }
];

export interface AppRoute {
    path: string,
    exact?: boolean,
    label: string,
    icon: React.ReactElement<SvgIconProps>,
    component: () => JSX.Element;
}